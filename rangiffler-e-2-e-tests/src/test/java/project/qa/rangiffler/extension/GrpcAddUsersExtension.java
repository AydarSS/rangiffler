package project.qa.rangiffler.extension;

import static org.awaitility.Awaitility.await;

import guru.qa.grpc.rangiffler.UserOuterClass;
import guru.qa.grpc.rangiffler.UserOuterClass.FriendsStatus;
import guru.qa.grpc.rangiffler.UserOuterClass.FriendshipAbout;
import guru.qa.grpc.rangiffler.UserOuterClass.FriendshipAction;
import guru.qa.grpc.rangiffler.UserOuterClass.UserByUsernameRequest;
import guru.qa.grpc.rangiffler.UserOuterClass.UserByUsernameResponse;
import io.grpc.StatusRuntimeException;
import io.qameta.allure.Step;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import project.qa.rangiffler.api.AuthApiClient;
import project.qa.rangiffler.extension.grpc.client.GrpcGeoClient;
import project.qa.rangiffler.extension.grpc.client.GrpcPhotoClient;
import project.qa.rangiffler.extension.grpc.client.GrpcUserClient;
import project.qa.rangiffler.extension.grpc.utils.TypeConverter;
import project.qa.rangiffler.model.Country;
import project.qa.rangiffler.model.FriendStatus;
import project.qa.rangiffler.model.Photo;
import project.qa.rangiffler.model.TestData;
import project.qa.rangiffler.model.User;
import project.qa.rangiffler.stub.UserServiceStub;

public class GrpcAddUsersExtension extends AddUsersExtension {

  private final AuthApiClient authApiClient = new AuthApiClient();
  private final GrpcPhotoClient photoClient = new GrpcPhotoClient();
  private final GrpcGeoClient geoClient = new GrpcGeoClient();
  private final GrpcUserClient userClient = new GrpcUserClient();

  @Override
  @Step("Создаем пользователя")
  protected User createUser(User user) {
    authApiClient.doRegister(user.username(), user.testData().password());
    awaitWhileUserSavedInUserdata(user.username());
    UserOuterClass.User protoUser = getUserByUsername(user.username());
    String STRING_EMPTY = "";
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
  @Step("Добавляем фото пользователю {username}")
  protected Photo addPhoto(String src, String username, String countryCode, String description) {
    return photoClient.addPhoto(src, countryCode, description, username);
  }

  @Override
  @Step("Добавляем лайки от пользователя {username} к фото {photoId}")
  protected void addLike(String username, String userId, String photoId) {
    photoClient.changeLike(username,
        UUID.fromString(userId),
        UUID.fromString(photoId));
  }

  @Override
  @Step("Сохраняем связанное лицо для пользователя {currentUsername}")
  protected User savePartner(User partner,
      String currentUsername,
      String currentUserId) {
    authApiClient.doRegister(partner.username(), partner.testData().password());
    awaitWhileUserSavedInUserdata(partner.username());
    User readyUser = updateUser(partner);
    switch (partner.friendStatus()) {
      case INVITATION_SENT -> sendInvitation(currentUsername, readyUser.id().toString());
      case FRIEND -> createFriendship(currentUsername, currentUserId, readyUser);
      case INVITATION_RECEIVED -> sendInvitation(readyUser.username(), currentUserId);
      case NOT_FRIEND -> {
      }
    }
    return readyUser;
  }

  @Step("Обновляем пользователя")
  private User updateUser(User user) {
    User updates;
    if(Objects.nonNull(user.country()) && !user.country().code().equals("")){
      Country country = geoClient.findByCode(user.country().code());
      updates = userClient.updateUser(user.withCountry(country));
    }else {
      updates = userClient.updateUser(user);
    }
    return updates;
  }

  @Step("Направляем заявку в друзья от пользователя {currentUsername}")
  private void sendInvitation(String currentUsername, String anotherUserId) {
    UserServiceStub.stub.identityFriendship(
        FriendshipAbout.newBuilder()
            .setRequesterUsername(currentUsername)
            .setAddresseeId(anotherUserId)
            .setFriendshipAction(FriendshipAction.ADD)
            .build()
    );
  }

  @Step("Создаем дружбу между пользователями")
  private void createFriendship(String currentUsername, String currentUserId, User user) {
    UserServiceStub.stub.identityFriendship(
        FriendshipAbout.newBuilder()
            .setRequesterUsername(currentUsername)
            .setAddresseeId(user.id().toString())
            .setFriendshipAction(FriendshipAction.ADD)
            .build());

    UserServiceStub.stub.identityFriendship(
        FriendshipAbout.newBuilder()
            .setRequesterUsername(user.username())
            .setAddresseeId(currentUserId)
            .setFriendshipAction(FriendshipAction.ACCEPT)
            .build());
  }

  @Step("Ожидаем появление пользователя {username} в сервисе userdata")
  private void awaitWhileUserSavedInUserdata(String username) {
    await()
        .atMost(30, TimeUnit.SECONDS)
        .pollInterval(Duration.ofSeconds(1))
        .until(() -> {
          try {
            UserByUsernameResponse response = UserServiceStub.stub.getUserByUsername(
                UserByUsernameRequest.newBuilder()
                    .setUsername(username)
                    .build());
            return !response.getUser().getId().isEmpty();
          } catch (StatusRuntimeException ignore) {
          }
          return false;
        });
  }

  @Step("Получаем пользователя {username} из сервиса userdata")
  private UserOuterClass.User getUserByUsername(String username) {
    UserByUsernameResponse response = UserServiceStub.stub.getUserByUsername(
        UserByUsernameRequest.newBuilder()
            .setUsername(username)
            .build());
    return response.getUser();
  }
}
