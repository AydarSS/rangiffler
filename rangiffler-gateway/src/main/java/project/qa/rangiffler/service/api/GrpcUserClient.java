package project.qa.rangiffler.service.api;

import com.google.protobuf.Empty;
import guru.qa.grpc.rangiffler.UserOuterClass;
import guru.qa.grpc.rangiffler.UserOuterClass.Country;
import guru.qa.grpc.rangiffler.UserOuterClass.CountryResponse;
import guru.qa.grpc.rangiffler.UserOuterClass.FriendshipAbout;
import guru.qa.grpc.rangiffler.UserOuterClass.FriendshipAction;
import guru.qa.grpc.rangiffler.UserOuterClass.LinkedUsersByUsernameRequest;
import guru.qa.grpc.rangiffler.UserOuterClass.PageableRequest;
import guru.qa.grpc.rangiffler.UserOuterClass.UserAbout;
import guru.qa.grpc.rangiffler.UserOuterClass.UserByUsernameRequest;
import guru.qa.grpc.rangiffler.UserOuterClass.UserByUsernameResponse;
import guru.qa.grpc.rangiffler.UserOuterClass.UsersPageableResponse;
import guru.qa.grpc.rangiffler.UserServiceGrpc;
import guru.qa.grpc.rangiffler.UserServiceGrpc.UserServiceBlockingStub;
import java.util.List;
import java.util.UUID;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import project.qa.rangiffler.model.query.Friendship;
import project.qa.rangiffler.model.query.PageableObjects;
import project.qa.rangiffler.model.query.PageableUsers;
import project.qa.rangiffler.model.query.User;
import project.qa.rangiffler.service.api.utils.ProtoTypeConverter;

@Component
public class GrpcUserClient implements UserClient {

  private static final Logger LOG = LoggerFactory.getLogger(GrpcUserClient.class);
  private UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub;
  private final ProtoTypeConverter typeConverter = new ProtoTypeConverter();

  @GrpcClient("grpcUserClient")
  public void setUserServiceBlockingStub(UserServiceBlockingStub userServiceBlockingStub) {
    this.userServiceBlockingStub = userServiceBlockingStub;
  }

  @Override
  public User byUsername(String username) {
    try {
      UserByUsernameResponse response = userServiceBlockingStub.getUserByUsername(
          UserByUsernameRequest.newBuilder()
              .setUsername(username)
              .build());
      return typeConverter.fromProtoToUser(response.getUser());
    } catch (Exception ex) {
      LOG.error("### Error while calling gRPC server ", ex);
      throw new RuntimeException(ex);
    }
  }

  @Override
  public PageableObjects<User> allUsers(String username, int page, int size, String searchQuery) {
    return getPagebleUsers(
        () -> userServiceBlockingStub
            .getAllUsers(buildLinkedUsersRequest(username, page, size, searchQuery)));
  }

  @Override
  public PageableObjects<User> friends(String username, int page, int size, String searchQuery) {
    return getPagebleUsers(
        () -> userServiceBlockingStub
            .getFriends(buildLinkedUsersRequest(username, page, size, searchQuery)));
  }

  @Override
  public PageableObjects<User> incomeInvitations(String username, int page, int size,
      String searchQuery) {
    return getPagebleUsers(
        () -> userServiceBlockingStub
            .getIncomeInvitations(buildLinkedUsersRequest(username, page, size, searchQuery)));
  }

  @Override
  public PageableObjects<User> outcomeInvitations(String username, int page, int size,
      String searchQuery) {
    return getPagebleUsers(
        () -> userServiceBlockingStub
            .getOutcomeInvitations(buildLinkedUsersRequest(username, page, size, searchQuery)));
  }

  @Override
  public User updateUser(User user) {
    UserOuterClass.User userGrpc = userServiceBlockingStub.updateUser(
        UserAbout.newBuilder()
            .setUsername(user.username())
            .setFirstname(user.firstname())
            .setSurname(user.surname())
            .setAvatar(user.avatar())
            .setCountry(Country.newBuilder()
                .setCode(user.country().code())
                .build())
            .build()
    );
    return typeConverter.fromProtoToUser(userGrpc);
  }

  @Override
  public User friendshipAction(Friendship friendship) {
    UserOuterClass.User userGrpc = userServiceBlockingStub.identityFriendship(
        FriendshipAbout.newBuilder()
            .setRequesterUsername(friendship.requesterUsername())
            .setAddresseeId(friendship.addressee().toString())
            .setFriendshipAction(FriendshipAction.valueOf(friendship.action().name()))
            .build()
    );
    return typeConverter.fromProtoToUser(userGrpc);
  }

  @Override
  public List<project.qa.rangiffler.model.query.Country> countries() {
    CountryResponse countryResponse = userServiceBlockingStub
        .getAllCountries(Empty.newBuilder().getDefaultInstanceForType());
    return typeConverter.fromProtoToListCountries(countryResponse.getCountryList());
  }

  private PageableObjects<User> getPagebleUsers(UsersRequest<UsersPageableResponse> request) {
    UsersPageableResponse usersPageableResponse = request.send();
    return new PageableUsers(
        typeConverter.fromProtoToListUsers(usersPageableResponse.getUsersList()),
        usersPageableResponse.getHasNext());
  }

  private LinkedUsersByUsernameRequest buildLinkedUsersRequest(String username, int page, int size,
      String searchQuery) {
    return LinkedUsersByUsernameRequest.newBuilder()
        .setUsername(username)
        .setPageInfo(pageInfo(page, size, searchQuery))
        .build();
  }

  private PageableRequest pageInfo(int page, int size, String searchQuery) {
    return PageableRequest.newBuilder()
        .setPage(page)
        .setSize(size)
        .setSearchQuery(searchQuery)
        .build();
  }
}
