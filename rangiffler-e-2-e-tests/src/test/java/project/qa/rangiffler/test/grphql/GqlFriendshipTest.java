package project.qa.rangiffler.test.grphql;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import project.qa.rangiffler.annotation.ApiLogin;
import project.qa.rangiffler.annotation.People;
import project.qa.rangiffler.annotation.TestUser;
import project.qa.rangiffler.annotation.Token;
import project.qa.rangiffler.annotation.UserParam;
import project.qa.rangiffler.annotation.WithPartners;
import project.qa.rangiffler.model.FriendStatus;
import project.qa.rangiffler.model.FriendshipAction;
import project.qa.rangiffler.model.User;
import project.qa.rangiffler.model.gql.GqlRequest;
import project.qa.rangiffler.model.gql.GqlUser;

public class GqlFriendshipTest extends BaseGraphQLTest {

  private final List<User> friends = new ArrayList<>();
  private final List<User> invitationSend = new ArrayList<>();
  private final List<User> invitationsReceived = new ArrayList<>();
  private final List<User> notFriend = new ArrayList<>();

  @DisplayName("Запрос на дружбу")
  @Test()
  @ApiLogin(
      user = @TestUser(generateRandom = true,
          partners = {
              @WithPartners(status = FriendStatus.NOT_FRIEND),
          }))
  void addFriendShouldBeSuccessTest(@UserParam User testUser,
      @Token String bearerToken,
      @People Map<User, FriendStatus> people) throws Exception {
    fillPeopleLists(people);
    executeTest(testUser,
        bearerToken,
        notFriend.get(0).id().toString(),
        FriendshipAction.ADD);
  }

  @DisplayName("Принять запрос на дружбу")
  @Test()
  @ApiLogin(
      user = @TestUser(generateRandom = true,
          partners = {
              @WithPartners(status = FriendStatus.INVITATION_RECEIVED),
          }))
  void acceptFriendShouldBeSuccessTest(@UserParam User testUser,
      @Token String bearerToken,
      @People Map<User, FriendStatus> people) throws Exception {
    fillPeopleLists(people);
    executeTest(testUser,
        bearerToken,
        invitationsReceived.get(0).id().toString(),
        FriendshipAction.ACCEPT);
  }

  @DisplayName("Отклонить запрос на дружбу")
  @Test()
  @ApiLogin(
      user = @TestUser(generateRandom = true,
          partners = {
              @WithPartners(status = FriendStatus.INVITATION_RECEIVED),
          }))
  void rejectFriendShouldBeSuccessTest(@UserParam User testUser,
      @Token String bearerToken,
      @People Map<User, FriendStatus> people) throws Exception {
    fillPeopleLists(people);
    executeTest(testUser,
        bearerToken,
        invitationsReceived.get(0).id().toString(),
        FriendshipAction.REJECT);
  }


  private void executeTest(
      User testUser,
      String bearerToken,
      String userID,
      FriendshipAction action) throws Exception {
    GqlRequest request = GqlRequest.friendShipIdentityGqlRequest(
        userID,
        action);

    final GqlUser response = gatewayGqlApiClient.identityFriendship(bearerToken, request);
    assertEquals(testUser.username(),
        response.getData().getFriendship().getUsername());
  }

  private void fillPeopleLists(Map<User, FriendStatus> people) {
    for (Entry<User, FriendStatus> person : people.entrySet()) {
      switch (person.getValue()) {
        case FRIEND -> friends.add(person.getKey());
        case INVITATION_RECEIVED -> invitationsReceived.add(person.getKey());
        case INVITATION_SENT -> invitationSend.add(person.getKey());
        case NOT_FRIEND -> notFriend.add(person.getKey());
      }
    }
  }

}
