package project.qa.rangiffler.test.grpc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import guru.qa.grpc.rangiffler.UserOuterClass;
import guru.qa.grpc.rangiffler.UserOuterClass.LinkedUsersByUsernameRequest;
import guru.qa.grpc.rangiffler.UserOuterClass.PageableRequest;
import guru.qa.grpc.rangiffler.UserOuterClass.UserAbout;
import guru.qa.grpc.rangiffler.UserOuterClass.UserByUsernameRequest;
import guru.qa.grpc.rangiffler.UserOuterClass.UserByUsernameResponse;
import guru.qa.grpc.rangiffler.UserOuterClass.UsersPageableResponse;
import io.qameta.allure.Step;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import project.qa.rangiffler.annotation.TestUser;
import project.qa.rangiffler.annotation.UserParam;
import project.qa.rangiffler.annotation.WithPartners;
import project.qa.rangiffler.db.DataSourceProvider;
import project.qa.rangiffler.db.Database;
import project.qa.rangiffler.extension.GrpcAddUsersExtension;
import project.qa.rangiffler.model.FriendStatus;
import project.qa.rangiffler.model.User;
import project.qa.rangiffler.stub.UserServiceStub;

@ExtendWith(GrpcAddUsersExtension.class)
public class UserdataServiceGrpcTest {

  private final NamedParameterJdbcOperations userJdbc = new NamedParameterJdbcTemplate(
      DataSourceProvider.INSTANCE.dataSource(Database.USERDATA));

  @DisplayName("Получение пользователя по username")
  @Test
  @TestUser(generateRandom = true,
      partners = {
          @WithPartners(status = FriendStatus.INVITATION_RECEIVED),
          @WithPartners(status = FriendStatus.INVITATION_SENT),
          @WithPartners(status = FriendStatus.FRIEND),

      })
  void byUsernameTest(@UserParam User user) {
    UserByUsernameResponse response = UserServiceStub.stub.getUserByUsername(
        UserByUsernameRequest.newBuilder()
            .setUsername(user.username())
            .build());
    assertEquals(response.getUser().getUsername(), user.username());
  }

  @DisplayName("Получение друзей, постраничное получение информации о друзьях")
  @Test
  @TestUser(generateRandom = true,
      partners = {
          @WithPartners(status = FriendStatus.FRIEND),
          @WithPartners(status = FriendStatus.FRIEND),
          @WithPartners(status = FriendStatus.FRIEND),
          @WithPartners(status = FriendStatus.FRIEND),
          @WithPartners(status = FriendStatus.FRIEND)
      })
  void friendsTest(@UserParam User user) {
    LinkedUsersByUsernameRequest requestFirst =
        buildLinkedUsersRequest(user.username(), 0, 2, null);
    UsersPageableResponse responseFirst = UserServiceStub.stub.getFriends(requestFirst);
    assertAll("Запрос 1, проверка постраничного вывода",
        () -> assertEquals(2, responseFirst.getUsersList().size(), "Размер массива"),
        () -> assertTrue(responseFirst.getHasNext(), "Есть ли еще страницы"));

    LinkedUsersByUsernameRequest requestSecond =
        buildLinkedUsersRequest(user.username(), 1, 2, null);
    UsersPageableResponse responseSecond = UserServiceStub.stub.getFriends(requestSecond);
    assertAll("Запрос 2, проверка постраничного вывода",
        () -> assertEquals(2, responseSecond.getUsersList().size(), "Размер массива"),
        () -> assertTrue(responseSecond.getHasNext(), "Есть ли еще страницы"));

    LinkedUsersByUsernameRequest requestThird =
        buildLinkedUsersRequest(user.username(), 2, 2, null);
    UsersPageableResponse responseThird = UserServiceStub.stub.getFriends(requestThird);
    assertAll("Запрос 3, проверка постраничного вывода",
        () -> assertEquals(1, responseThird.getUsersList().size(), "Размер массива"),
        () -> assertFalse(responseThird.getHasNext(), "Есть ли еще страницы"));
  }

  @DisplayName("Получение исходящих заявок с searchQuery")
  @Test
  @TestUser(generateRandom = true,
      partners = {
          @WithPartners(status = FriendStatus.INVITATION_SENT),
          @WithPartners(status = FriendStatus.INVITATION_SENT)
      })
  void invitationSendTest(@UserParam User user) {

    String sql = String.format("""
        SELECT u2.username  as searchQuery
        FROM user u
        INNER JOIN friendship f on u.id = f.requester_id
        INNER JOIN user u2  on u2.id = f.addressee_id
        WHERE u.username = '%s'
        AND f.status='PENDING'
        LIMIT 1;
        """, user.username());
    String searchQuery = (String) userJdbc.queryForList(sql, new HashMap<>()).get(0)
        .get("searchQuery");

    LinkedUsersByUsernameRequest request =
        buildLinkedUsersRequest(user.username(), 0, 5, searchQuery);
    UsersPageableResponse response = UserServiceStub.stub.getOutcomeInvitations(request);
    List<UserOuterClass.User> users = response.getUsersList();

    assertAll("Проверка поиска подстроки",
        () -> assertThat(users)
            .as("Подстрока %s содержится в массиве", searchQuery)
            .extracting(UserOuterClass.User::getUsername).contains(searchQuery));
  }

  @DisplayName("Получение входящих заявок с пустым списокм")
  @Test
  @TestUser(generateRandom = true)
  void invitationReceiveTest(@UserParam User user) {

    LinkedUsersByUsernameRequest request =
        buildLinkedUsersRequest(user.username(), 0, 5, null);

    UsersPageableResponse response = UserServiceStub.stub.getOutcomeInvitations(request);
    List<UserOuterClass.User> users = response.getUsersList();

    assertAll("Проверка что список пуст",
        () -> assertEquals(0, users.size(), "Список пуст"),
        () -> assertFalse(response.getHasNext(), "Нет следующей страницы"));
  }

  @DisplayName("Обновление юзера")
  @Test
  @TestUser(generateRandom = true)
  void userUpdateTest(@UserParam User user) {
    String countryId = UUID.randomUUID().toString();
    UserOuterClass.User userGrpc = UserServiceStub.stub.updateUser(
        UserAbout.newBuilder()
            .setUsername(user.username())
            .setFirstname("firstname")
            .setSurname("surname")
            .setAvatar("avatar")
            .setCountryId(countryId)
            .build());

    assertAll("Проверка что пользователь обновлен",
        () -> assertEquals("firstname", userGrpc.getFirstname(), "Проверка firstname"),
        () -> assertEquals("surname", userGrpc.getSurname(), "Проверка surname"),
        () -> assertEquals("avatar", userGrpc.getAvatar(), "Проверка avatar"),
        () -> assertEquals(countryId, userGrpc.getCountryId(), "Проверка countryId"));
  }


  @Step("Формируем данные для {page} запроса")
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
        .setSearchQuery(
            Objects.isNull(searchQuery) ? "" : searchQuery)
        .build();
  }

}
