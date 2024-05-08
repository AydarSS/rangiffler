package project.qa.rangiffler.test.grphql;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import project.qa.rangiffler.annotation.ApiLogin;
import project.qa.rangiffler.annotation.GqlRequestFile;
import project.qa.rangiffler.annotation.TestUser;
import project.qa.rangiffler.annotation.Token;
import project.qa.rangiffler.annotation.UserParam;
import project.qa.rangiffler.annotation.WithPartners;
import project.qa.rangiffler.model.FriendStatus;
import project.qa.rangiffler.model.User;
import project.qa.rangiffler.model.gql.GqlRequest;
import project.qa.rangiffler.model.gql.GqlUser;

public class GqlUsersTest extends BaseGraphQLTest {

  @DisplayName("Получение пользователя")
  @Test()
  @ApiLogin(
      user = @TestUser(generateRandom = true)
  )
  void currentUserShouldBeReturnedTest(@UserParam User testUser,
      @Token String bearerToken,
      @GqlRequestFile("gql/GetUser.json") GqlRequest request) throws Exception {

    final GqlUser response = gatewayGqlApiClient.currentUser(bearerToken, request);
    assertEquals(
        testUser.username(),
        response.getData().getUser().getUsername()
    );
  }

  @DisplayName("Изменение пользователя")
  @Test()
  @ApiLogin(
      user = @TestUser(generateRandom = true))
  void updatedUserShouldBeReturnedTest(
      @Token String bearerToken,
      @GqlRequestFile("gql/UpdateUser.json") GqlRequest request) throws Exception {

    final GqlUser response = gatewayGqlApiClient.updateUser(bearerToken, request);
    assertEquals("surname", response.getData().getUser().getSurname());
  }

  @DisplayName("Запрос друзей пользователя")
  @Test()
  @ApiLogin(
      user = @TestUser(generateRandom = true,
          partners = {
              @WithPartners(status = FriendStatus.FRIEND),
          }))
  void friendsShouldBeReturnedTest(
      @Token String bearerToken,
      @GqlRequestFile("gql/GetFriends.json") GqlRequest request) throws Exception {
    final GqlUser response = gatewayGqlApiClient.getLinkedPerson(bearerToken, request);

    assertAll(
        () -> assertEquals(1, response.getData().getUser().getFriends().getEdges().size(),
            "Количество друзей"),
        () -> assertEquals(FriendStatus.FRIEND,
            response.getData().getUser().getFriends().getEdges().get(0).getUserNode()
                .getFriendState())
    );
  }

  @DisplayName("Запрос исходящих заявок пользователя")
  @Test()
  @ApiLogin(
      user = @TestUser(generateRandom = true,
          partners = {
              @WithPartners(status = FriendStatus.FRIEND),
              @WithPartners(status = FriendStatus.INVITATION_SENT),
              @WithPartners(status = FriendStatus.INVITATION_RECEIVED),
          }))
  void outcomeShouldBeReturnedTest(
      @Token String bearerToken,
      @GqlRequestFile("gql/GetOutcomeInvitations.json") GqlRequest request) throws Exception {
    final GqlUser response = gatewayGqlApiClient.getLinkedPerson(bearerToken, request);

    assertAll(
        () -> assertEquals(1,
            response.getData().getUser().getOutcomeInvitations().getEdges().size(),
            "Количество исходящих заявок"),
        () -> assertEquals(FriendStatus.INVITATION_SENT,
            response.getData().getUser().getOutcomeInvitations().getEdges().get(0).getUserNode()
                .getFriendState())
    );
  }

  @DisplayName("Запрос входящих заявок пользователя")
  @Test()
  @ApiLogin(
      user = @TestUser(generateRandom = true,
          partners = {
              @WithPartners(status = FriendStatus.FRIEND),
              @WithPartners(status = FriendStatus.INVITATION_SENT),
              @WithPartners(status = FriendStatus.INVITATION_RECEIVED),
          }))
  void incomeShouldBeReturnedTest(
      @Token String bearerToken,
      @GqlRequestFile("gql/GetIncomeInvitations.json") GqlRequest request) throws Exception {
    final GqlUser response = gatewayGqlApiClient.getLinkedPerson(bearerToken, request);

    assertAll(
        () -> assertEquals(1, response.getData().getUser().getIncomeInvitations().getEdges().size(),
            "Количество исходящих заявок"),
        () -> assertEquals(FriendStatus.INVITATION_RECEIVED,
            response.getData().getUser().getIncomeInvitations().getEdges().get(0).getUserNode()
                .getFriendState())
    );
  }

  @DisplayName("Запрос друзей с пагинацией")
  @Test()
  @ApiLogin(
      user = @TestUser(generateRandom = true,
          partners = {
              @WithPartners(status = FriendStatus.FRIEND),
              @WithPartners(status = FriendStatus.FRIEND),
              @WithPartners(status = FriendStatus.FRIEND),
          }))
  void friendsPaginationShouldBeReturnedTest(
      @Token String bearerToken,
      @GqlRequestFile("gql/GetFriendsWithSize2.json") GqlRequest request) throws Exception {
    final GqlUser response = gatewayGqlApiClient.getLinkedPerson(bearerToken, request);

    assertAll(
        () -> assertEquals(2, response.getData().getUser().getFriends().getEdges().size(),
            "Количество друзей"),
        () -> assertEquals(FriendStatus.FRIEND,
            response.getData().getUser().getFriends().getEdges().get(0).getUserNode()
                .getFriendState()),
        () -> assertEquals(FriendStatus.FRIEND,
            response.getData().getUser().getFriends().getEdges().get(1).getUserNode()
                .getFriendState())
    );
  }

}
