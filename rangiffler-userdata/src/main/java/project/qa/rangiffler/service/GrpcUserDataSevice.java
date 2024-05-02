package project.qa.rangiffler.service;

import static project.qa.rangiffler.data.utils.EntityToGrpcConverter.setEmptyValueIfNull;

import guru.qa.grpc.rangiffler.UserOuterClass.FriendsStatus;
import guru.qa.grpc.rangiffler.UserOuterClass.FriendshipAbout;
import guru.qa.grpc.rangiffler.UserOuterClass.FriendshipAction;
import guru.qa.grpc.rangiffler.UserOuterClass.LinkedUsersByUsernameRequest;
import guru.qa.grpc.rangiffler.UserOuterClass.User;
import guru.qa.grpc.rangiffler.UserOuterClass.UserAbout;
import guru.qa.grpc.rangiffler.UserOuterClass.UserByUsernameRequest;
import guru.qa.grpc.rangiffler.UserOuterClass.UserByUsernameResponse;
import guru.qa.grpc.rangiffler.UserOuterClass.UsersPageableResponse;
import guru.qa.grpc.rangiffler.UserServiceGrpc;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import project.qa.rangiffler.data.FriendshipEntity;
import project.qa.rangiffler.data.FriendshipStatus;
import project.qa.rangiffler.data.UserEntity;
import project.qa.rangiffler.data.repository.FriendshipRepository;
import project.qa.rangiffler.data.repository.UserRepository;
import project.qa.rangiffler.data.utils.EntityToGrpcConverter;

@GrpcService
public class GrpcUserDataSevice extends UserServiceGrpc.UserServiceImplBase {

  private final UserRepository userRepository;
  private final FriendshipRepository friendshipRepository;
  private final String STRING_EMPTY = "";

  @Autowired
  public GrpcUserDataSevice(UserRepository userRepository,
      FriendshipRepository friendshipRepository) {
    this.userRepository = userRepository;
    this.friendshipRepository = friendshipRepository;
  }

  @Override
  public void getAllUsers(LinkedUsersByUsernameRequest request,
      StreamObserver<UsersPageableResponse> responseObserver) {
    UsersPageableResponse response = getPagebleUsers(
        () -> userRepository.findByUsernameNot(
            request.getUsername(),
            pageableFrom(request)),
        FriendsStatus.NOT_FRIEND);

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void getUserByUsername(UserByUsernameRequest request,
      StreamObserver<UserByUsernameResponse> responseObserver) {
    Optional<UserEntity> user =
        userRepository.findByUsername(request.getUsername());
    if (user.isPresent()) {
      UserByUsernameResponse response = UserByUsernameResponse.newBuilder()
          .setUser(EntityToGrpcConverter.fromUserEntity(user.get()))
          .build();

      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } else {
      responseObserver.onError(Status.INVALID_ARGUMENT
          .withDescription(String.format("User %s not found",
              request.getUsername()))
          .asRuntimeException());
    }

  }

  @Override
  public void getFriends(LinkedUsersByUsernameRequest request,
      StreamObserver<UsersPageableResponse> responseObserver) {
    UserEntity user = buildUserEntityFromUsername(request.getUsername());
    UsersPageableResponse response;
    if (isSearchQueryIsEmptyIn(request) && isPageableIsNotExistsIn(request)) {
      response = getPagebleUsers(
          () -> userRepository.findFriends(user),
          FriendsStatus.FRIEND);
    } else if (isSearchQueryIsEmptyIn(request)) {
      response = getPagebleUsers(
          () -> userRepository.findFriends(user,
              pageableFrom(request)),
          FriendsStatus.FRIEND);
    } else if (isPageableIsNotExistsIn(request)) {
      response = getPagebleUsers(
          () -> userRepository.findFriends(user, searchQueryFrom(request)),
          FriendsStatus.FRIEND);
    } else {
      response = getPagebleUsers(
          () -> userRepository.findFriends(user,
              pageableFrom(request),
              searchQueryFrom(request)),
          FriendsStatus.FRIEND);
    }

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void getIncomeInvitations(LinkedUsersByUsernameRequest request,
      StreamObserver<UsersPageableResponse> responseObserver) {
    UserEntity user = buildUserEntityFromUsername(request.getUsername());
    UsersPageableResponse response;

    if (isSearchQueryIsEmptyIn(request) && isPageableIsNotExistsIn(request)) {
      response = getPagebleUsers(
          () -> userRepository.findIncomeInvitations(user),
          FriendsStatus.INVITATION_RECEIVED);
    } else if (isSearchQueryIsEmptyIn(request)) {
      response = getPagebleUsers(
          () -> userRepository.findIncomeInvitations(user,
              PageRequest.of(request.getPageInfo().getPage(), request.getPageInfo().getSize())),
          FriendsStatus.INVITATION_RECEIVED);
    } else if (isPageableIsNotExistsIn(request)) {
      response = getPagebleUsers(
          () -> userRepository.findIncomeInvitations(user,
              request.getPageInfo().getSearchQuery()),
          FriendsStatus.INVITATION_RECEIVED);
    } else {
      response = getPagebleUsers(
          () -> userRepository.findIncomeInvitations(user,
              PageRequest.of(request.getPageInfo().getPage(), request.getPageInfo().getSize()),
              request.getPageInfo().getSearchQuery()),
          FriendsStatus.INVITATION_RECEIVED);
    }

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void getOutcomeInvitations(LinkedUsersByUsernameRequest request,
      StreamObserver<UsersPageableResponse> responseObserver) {
    UserEntity user = buildUserEntityFromUsername(request.getUsername());
    UsersPageableResponse response;

    if (isSearchQueryIsEmptyIn(request) && isPageableIsNotExistsIn(request)) {
      response = getPagebleUsers(
          () -> userRepository.findOutcomeInvitations(user),
          FriendsStatus.INVITATION_SENT);
    } else if (isSearchQueryIsEmptyIn(request)) {
      response = getPagebleUsers(
          () -> userRepository.findOutcomeInvitations(user, pageableFrom(request)),
          FriendsStatus.INVITATION_SENT);
    } else if (isPageableIsNotExistsIn(request)) {
      response = getPagebleUsers(
          () -> userRepository.findOutcomeInvitations(user, searchQueryFrom(request)),
          FriendsStatus.INVITATION_SENT);
    } else {
      response = getPagebleUsers(
          () -> userRepository.findOutcomeInvitations(user,
              pageableFrom(request),
              searchQueryFrom(request)),
          FriendsStatus.INVITATION_SENT);
    }

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void updateUser(UserAbout request, StreamObserver<User> responseObserver) {
    UserEntity userEntity = userRepository.findByUsername(request.getUsername()).get();

    userEntity.setCountryId(UUID.fromString(request.getCountryId()));
    userEntity.setFirstname(request.getFirstname());
    userEntity.setSurname(request.getSurname());
    userEntity.setAvatar(request.getAvatar());

    UserEntity saved = userRepository.save(userEntity);

    User response = EntityToGrpcConverter.fromUserEntity(saved);

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void identityFriendship(FriendshipAbout request, StreamObserver<User> responseObserver) {
    UserEntity requester = null;
    UserEntity addressee;

    if (request.getFriendshipAction().equals(FriendshipAction.ADD)) {
      requester = userRepository.findByUsername(request.getRequesterUsername()).get();
      addressee = userRepository.findById(UUID.fromString(request.getAddresseeId())).get();

      FriendshipEntity friendshipEntity = new FriendshipEntity();
      friendshipEntity.setRequester(requester);
      friendshipEntity.setAddressee(addressee);

      friendshipEntity.setCreatedDate(LocalDateTime.now());
      friendshipEntity.setStatus(FriendshipStatus.PENDING);
      friendshipRepository.save(friendshipEntity);

    } else if (request.getFriendshipAction().equals(FriendshipAction.ACCEPT)) {
      requester = userRepository.findByUsername(request.getRequesterUsername()).get();
      addressee = userRepository.findById(UUID.fromString(request.getAddresseeId())).get();

      FriendshipEntity friendshipEntity = new FriendshipEntity();
      friendshipEntity.setRequester(addressee);
      friendshipEntity.setAddressee(requester);

      friendshipEntity.setStatus(FriendshipStatus.ACCEPTED);
      friendshipEntity.setCreatedDate(LocalDateTime.now());
      friendshipRepository.save(friendshipEntity);
    } else if (request.getFriendshipAction().equals(FriendshipAction.REJECT)) {
      requester = userRepository.findByUsername(request.getRequesterUsername()).get();
      addressee = userRepository.findById(UUID.fromString(request.getAddresseeId())).get();

      FriendshipEntity friendshipEntity = new FriendshipEntity();
      friendshipEntity.setRequester(addressee);
      friendshipEntity.setAddressee(requester);

      friendshipRepository.delete(friendshipEntity);
    } else if (request.getFriendshipAction().equals(FriendshipAction.DELETE)) {
      requester = userRepository.findByUsername(request.getRequesterUsername()).get();
      addressee = userRepository.findById(UUID.fromString(request.getAddresseeId())).get();
      Optional<FriendshipEntity> friendship = friendshipRepository.findFriendship(requester,
          addressee);
      if (friendship.isPresent()) {
        friendshipRepository.delete(friendship.get());
      }
    }

    User response = EntityToGrpcConverter.fromUserEntity(requester);

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  private UsersPageableResponse getPagebleUsers(UsersResponse<Slice<UserEntity>> request,
      FriendsStatus friendsStatus) {
    Slice<UserEntity> users = request.get();
    if (!users.hasContent()) {
      return UsersPageableResponse.getDefaultInstance();
    }

    return UsersPageableResponse.newBuilder()
        .addAllUsers(users
            .getContent()
            .stream()
            .map(userEntity -> User.newBuilder()
                .setId(userEntity.getId().toString())
                .setUsername(userEntity.getUsername())
                .setFirstname(setEmptyValueIfNull(userEntity.getFirstname()))
                .setSurname(setEmptyValueIfNull(userEntity.getSurname()))
                .setAvatar(setEmptyValueIfNull(userEntity.getAvatar()))
                .setFriendStatus(friendsStatus)
                .setCountryId(userEntity.getCountryId().toString())
                .build())
            .toList())
        .setHasNext(users.hasNext())
        .build();
  }

  private boolean isSearchQueryIsEmptyIn(LinkedUsersByUsernameRequest request) {
    return request.getPageInfo().getSearchQuery().equals(STRING_EMPTY);
  }

  private boolean isPageableIsNotExistsIn(LinkedUsersByUsernameRequest request) {
    return request.getPageInfo().getPage() == 0 && request.getPageInfo().getSize() == 0;
  }

  private UserEntity buildUserEntityFromUsername(String username) {
    return userRepository.findByUsername(username).get();
  }

  private Pageable pageableFrom(LinkedUsersByUsernameRequest request) {
    return PageRequest.of(request.getPageInfo().getPage(), request.getPageInfo().getSize());
  }

  private String searchQueryFrom(LinkedUsersByUsernameRequest request) {
    return request.getPageInfo().getSearchQuery();
  }
}
