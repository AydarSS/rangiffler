package project.qa.rangiffler.service.grpc;

import guru.qa.grpc.rangiffler.UserOuterClass.FriendsStatus;
import guru.qa.grpc.rangiffler.UserOuterClass.FriendshipAbout;
import guru.qa.grpc.rangiffler.UserOuterClass.LinkedUsersByUsernameRequest;
import guru.qa.grpc.rangiffler.UserOuterClass.User;
import guru.qa.grpc.rangiffler.UserOuterClass.UserAbout;
import guru.qa.grpc.rangiffler.UserOuterClass.UserByUsernameRequest;
import guru.qa.grpc.rangiffler.UserOuterClass.UserByUsernameResponse;
import guru.qa.grpc.rangiffler.UserOuterClass.UsersPageableResponse;
import guru.qa.grpc.rangiffler.UserServiceGrpc;
import io.grpc.stub.StreamObserver;
import java.util.List;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import project.qa.rangiffler.data.UserEntity;
import project.qa.rangiffler.service.grpc.utils.EntityToGrpcConverter;
import project.qa.rangiffler.service.api.UserDataService;
import project.qa.rangiffler.service.api.UserDataServiceImpl;
import project.qa.rangiffler.service.grpc.utils.GrpcFriendshipResolver;

@GrpcService
public class GrpcUserDataReceiver extends UserServiceGrpc.UserServiceImplBase {

  private final UserDataService service;
  private final GrpcFriendshipResolver grpcFriendshipResolver;

  @Autowired
  public GrpcUserDataReceiver(UserDataServiceImpl service,
      GrpcFriendshipResolver grpcFriendshipResolver) {
    this.service = service;
    this.grpcFriendshipResolver = grpcFriendshipResolver;
  }

  @Override
  public void getAllUsers(LinkedUsersByUsernameRequest request,
      StreamObserver<UsersPageableResponse> responseObserver) {
    UsersPageableResponse response;
    String currentUsername = request.getUsername();
    UserEntity currentUser = service.getCurrentUserByUsername(currentUsername);

    Slice<UserEntity> users = service.findAllUsers(currentUsername,
        pageableFrom(request),
        request.getPageInfo().getSearchQuery());

    if (!users.hasContent()) {
      response = UsersPageableResponse.getDefaultInstance();
      responseObserver.onNext(response);
      responseObserver.onCompleted();
      return;
    }

    List<User> grpcUsers = users.getContent().stream()
        .map(userEntity ->
            EntityToGrpcConverter.withFriendStatusResolver(
                userEntity,
                () -> grpcFriendshipResolver.resolveFriendShipStatus(currentUser, userEntity)))
        .toList();

    response = UsersPageableResponse.newBuilder()
        .addAllUsers(grpcUsers)
        .setHasNext(users.hasNext())
        .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void getUserByUsername(UserByUsernameRequest request,
      StreamObserver<UserByUsernameResponse> responseObserver) {
    UserEntity user = service.findByUsername(request.getUsername());

    UserByUsernameResponse response = UserByUsernameResponse.newBuilder()
        .setUser(EntityToGrpcConverter.to(user))
        .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void getFriends(LinkedUsersByUsernameRequest request,
      StreamObserver<UsersPageableResponse> responseObserver) {
    Slice<UserEntity> friends = service.findFriends(request.getUsername(),
        pageableFrom(request),
        searchQueryFrom(request));

    UsersPageableResponse response = getPageableUsersResponse(friends, FriendsStatus.FRIEND);

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void getIncomeInvitations(LinkedUsersByUsernameRequest request,
      StreamObserver<UsersPageableResponse> responseObserver) {
    Slice<UserEntity> incomeInvitations = service.findIncomeInvitations(request.getUsername(),
        pageableFrom(request),
        searchQueryFrom(request));
    UsersPageableResponse response = getPageableUsersResponse(incomeInvitations,
        FriendsStatus.INVITATION_RECEIVED);

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void getOutcomeInvitations(LinkedUsersByUsernameRequest request,
      StreamObserver<UsersPageableResponse> responseObserver) {
    Slice<UserEntity> outcomeInvitations = service.findOutcomeInvitations(request.getUsername(),
        pageableFrom(request),
        searchQueryFrom(request));
    UsersPageableResponse response = getPageableUsersResponse(outcomeInvitations,
        FriendsStatus.INVITATION_SENT);

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void updateUser(UserAbout request, StreamObserver<User> responseObserver) {
    UserEntity updated = service.updateUser(
        request.getUsername(),
        request.getFirstname(),
        request.getSurname(),
        request.getAvatar(),
        request.getCountryId());

    User response = EntityToGrpcConverter.to(updated);

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void identityFriendship(FriendshipAbout request, StreamObserver<User> responseObserver) {
    String currentUsername = request.getRequesterUsername();
    String linkedUserId = request.getAddresseeId();

    switch (request.getFriendshipAction()) {
      case ADD -> service.addFriendshipRequest(linkedUserId, currentUsername);
      case ACCEPT -> service.acceptFriendshipRequest(linkedUserId, currentUsername);
      case REJECT -> service.rejectFriendshipRequest(linkedUserId, currentUsername);
      case DELETE -> service.deleteFriendshipRequest(linkedUserId, currentUsername);
    }

    UserEntity currentUser = service.findByUsername(currentUsername);
    User response = EntityToGrpcConverter.to(currentUser);

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }


  private UsersPageableResponse getPageableUsersResponse(
      Slice<UserEntity> users,
      FriendsStatus friendsStatus) {

    if (!users.hasContent()) {
      return UsersPageableResponse.getDefaultInstance();
    }
    return UsersPageableResponse.newBuilder()
        .addAllUsers(users
            .getContent()
            .stream()
            .map(user -> EntityToGrpcConverter.withFriendStatus(user, friendsStatus))
            .toList())
        .setHasNext(users.hasNext())
        .build();
  }

  private Pageable pageableFrom(LinkedUsersByUsernameRequest request) {
    return PageRequest.of(request.getPageInfo().getPage(), request.getPageInfo().getSize());
  }

  private String searchQueryFrom(LinkedUsersByUsernameRequest request) {
    return request.getPageInfo().getSearchQuery();
  }
}
