package project.qa.rangiffler.extension;

import static org.awaitility.Awaitility.await;

import com.github.javafaker.Faker;
import com.google.common.base.Stopwatch;
import guru.qa.grpc.rangiffler.UserOuterClass;
import guru.qa.grpc.rangiffler.UserOuterClass.FriendsStatus;
import guru.qa.grpc.rangiffler.UserOuterClass.FriendshipAbout;
import guru.qa.grpc.rangiffler.UserOuterClass.FriendshipAction;
import guru.qa.grpc.rangiffler.UserOuterClass.UserByUsernameRequest;
import guru.qa.grpc.rangiffler.UserOuterClass.UserByUsernameResponse;
import io.grpc.StatusRuntimeException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import project.qa.rangiffler.api.AuthApiClient;
import project.qa.rangiffler.model.Country;
import project.qa.rangiffler.model.FriendStatus;
import project.qa.rangiffler.model.TestData;
import project.qa.rangiffler.model.User;
import project.qa.rangiffler.stub.UserServiceStub;

public class GrpcAddUsersExtension extends AddUsersExtension{

  private final AuthApiClient authApiClient = new AuthApiClient();
  private final String STRING_EMPTY = "";
  private final String PASSWORD = "12345";

  @Override
  protected User createUser(User user) {
    authApiClient.doRegister(user.username(), user.testData().password());
    awaitWhileUserSavedInUserdata(user.username());
    UserOuterClass.User protoUser = getUserByUsername(user.username());
    return new User(
        UUID.fromString(protoUser.getId()),
        protoUser.getUsername(),
        protoUser.getFirstname(),
        protoUser.getSurname(),
        protoUser.getAvatar(),
        protoUser.getFriendStatus().equals(FriendsStatus.NOT_FRIEND) ||
            protoUser.getFriendStatus().equals(FriendsStatus.UNSPECIFIED)
            ?
            null :
            FriendStatus.valueOf(protoUser.getFriendStatus().name()),
        new ArrayList<>(),
        new ArrayList<>(),
        new ArrayList<>(),
        protoUser.getCountryId().equals(STRING_EMPTY) ?
            null :
            Country.withOnlyId(protoUser.getCountryId()),
        new TestData(user.testData().password()));
  }

  @Override
  protected void savePartner(FriendStatus friendStatus, String currentUsername, String currentUserId) {
    Faker faker = new Faker();
    String username = faker.name().username();
    authApiClient.doRegister(username, PASSWORD);
    awaitWhileUserSavedInUserdata(username);
    UserOuterClass.User protoUser = getUserByUsername(username);
    switch (friendStatus) {
      case INVITATION_SENT -> sendInvitation(currentUsername, protoUser.getId());
      case FRIEND -> createFriendship(currentUsername,currentUserId, protoUser);
      case INVITATION_RECEIVED -> sendInvitation(protoUser.getUsername(),currentUserId);
      case NOT_FRIEND -> {}
    }
  }

  private void sendInvitation(String currentUsername, String anotherUserId){
    UserServiceStub.stub.identityFriendship(
        FriendshipAbout.newBuilder()
            .setRequesterUsername(currentUsername)
            .setAddresseeId(anotherUserId)
            .setFriendshipAction(FriendshipAction.ADD)
            .build()
    );
  }

  private void createFriendship(String currentUsername, String currentUserId, UserOuterClass.User protoUser) {
    UserServiceStub.stub.identityFriendship(
        FriendshipAbout.newBuilder()
            .setRequesterUsername(currentUsername)
            .setAddresseeId(protoUser.getId())
            .setFriendshipAction(FriendshipAction.ADD)
            .build());

    UserServiceStub.stub.identityFriendship(
        FriendshipAbout.newBuilder()
            .setRequesterUsername(protoUser.getUsername())
            .setAddresseeId(currentUserId)
            .setFriendshipAction(FriendshipAction.ACCEPT)
            .build());
  }

  private void awaitWhileUserSavedInUserdata(String username) {
    await()
        .atMost(30,TimeUnit.SECONDS)
        .pollInterval(Duration.ofSeconds(1))
        .until(()-> {
          try {
            UserByUsernameResponse response = UserServiceStub.stub.getUserByUsername(
                UserByUsernameRequest.newBuilder()
                    .setUsername(username)
                    .build());
            return !response.getUser().getId().isEmpty();
          }catch (StatusRuntimeException ignore) {
          }
          return false;
        });
  }

  private UserOuterClass.User getUserByUsername (String username) {
    UserByUsernameResponse response = UserServiceStub.stub.getUserByUsername(
        UserByUsernameRequest.newBuilder()
            .setUsername(username)
            .build());
    return response.getUser();
  }
}
