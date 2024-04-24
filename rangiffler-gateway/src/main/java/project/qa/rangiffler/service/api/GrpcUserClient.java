package project.qa.rangiffler.service.api;

import guru.qa.grpc.rangiffler.UserOuterClass.LinkedUsersByUsernameRequest;
import guru.qa.grpc.rangiffler.UserOuterClass.PageableRequest;
import guru.qa.grpc.rangiffler.UserOuterClass.UserByUsernameRequest;
import guru.qa.grpc.rangiffler.UserOuterClass.UserByUsernameResponse;
import guru.qa.grpc.rangiffler.UserOuterClass.UsersPageableResponse;
import guru.qa.grpc.rangiffler.UserServiceGrpc;
import guru.qa.grpc.rangiffler.UserServiceGrpc.UserServiceBlockingStub;
import io.grpc.Status;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import project.qa.rangiffler.model.query.PageableObjects;
import project.qa.rangiffler.model.query.PageableUsers;
import project.qa.rangiffler.model.query.User;
import project.qa.rangiffler.utils.ProtoTypeConverter;

@Component
public class GrpcUserClient implements UserClient {

  private static final Logger LOG = LoggerFactory.getLogger(GrpcUserClient.class);
  private UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub;
  private final ProtoTypeConverter typeConverter = new ProtoTypeConverter();

  @GrpcClient("grpcUserClient")
  public void setUserServiceBlockingStub(
      UserServiceBlockingStub userServiceBlockingStub) {
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
