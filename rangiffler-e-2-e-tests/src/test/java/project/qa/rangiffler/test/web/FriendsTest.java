package project.qa.rangiffler.test.web;

import static project.qa.rangiffler.page.message.SuccessMsg.INVITATION_ACCEPTED;
import static project.qa.rangiffler.page.message.SuccessMsg.INVITATION_DECLINED;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.junit5.AllureJunit5;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import project.qa.rangiffler.annotation.ApiLogin;
import project.qa.rangiffler.annotation.People;
import project.qa.rangiffler.annotation.TestUser;
import project.qa.rangiffler.annotation.WithPartners;
import project.qa.rangiffler.extension.AddPeopleInMethodResolver;
import project.qa.rangiffler.extension.ApiLoginExtension;
import project.qa.rangiffler.extension.BrowserExtension;
import project.qa.rangiffler.extension.ContextHolderExtension;
import project.qa.rangiffler.extension.GrpcAddUsersExtension;
import project.qa.rangiffler.model.FriendStatus;
import project.qa.rangiffler.model.User;
import project.qa.rangiffler.page.FriendsPage;
import project.qa.rangiffler.page.message.SuccessMsg;

@ExtendWith({ContextHolderExtension.class, AllureJunit5.class, BrowserExtension.class,
    GrpcAddUsersExtension.class, ApiLoginExtension.class, AddPeopleInMethodResolver.class})
public class FriendsTest {

  private final List<User> friends = new ArrayList<>();
  private final List<User> invitationSend = new ArrayList<>();
  private final List<User> invitationsReceived = new ArrayList<>();
  private final List<User> notFriend = new ArrayList<>();

  @BeforeEach()
  void openUrl() {
    Selenide.open(FriendsPage.URL);
  }

  @Test
  @DisplayName("Проверим отображение друзей")
  @ApiLogin(
      user = @TestUser(generateRandom = true,
          partners = {
              @WithPartners(status = FriendStatus.INVITATION_SENT),
              @WithPartners(status = FriendStatus.INVITATION_RECEIVED),
              @WithPartners(firstname = "Filatov",
                  lastname = "Ivanov",
                  countryCode = "ru",
                  status = FriendStatus.FRIEND),
              @WithPartners(status = FriendStatus.FRIEND),
              @WithPartners(status = FriendStatus.NOT_FRIEND),
              @WithPartners(status = FriendStatus.NOT_FRIEND),
          }))
  void friendsShouldBeVisibleTest(@People Map<User, FriendStatus> people) {
    fillPeopleLists(people);
    User friendUser = friends.stream().filter(u -> u.firstname().equals("Filatov")).findAny().get();
    new FriendsPage()
        .checkCountRowInTable(friends.size())
        .checkUsername(friends.get(0).username())
        .checkUsername(friends.get(1).username())
        .checkFlag(friendUser.username(), "Russian Federation")
        .checkRemoveAction(friendUser.username());
  }

  @Test
  @DisplayName("Проверим отображение входящих заявок")
  @ApiLogin(
      user = @TestUser(generateRandom = true,
          partners = {
              @WithPartners(status = FriendStatus.INVITATION_SENT),
              @WithPartners(status = FriendStatus.INVITATION_RECEIVED),
              @WithPartners(status = FriendStatus.FRIEND),
              @WithPartners(status = FriendStatus.NOT_FRIEND),
          }))
  void incomeInvitationsShouldBeVisibleTest(@People Map<User, FriendStatus> people) {
    fillPeopleLists(people);
    new FriendsPage()
        .goToIncomeInvitations()
        .checkCountRowInTable(1)
        .checkUsername(invitationsReceived.get(0).username())
        .checkAcceptAction(invitationsReceived.get(0).username())
        .checkDeclineAction(invitationsReceived.get(0).username());
  }

  @Test
  @DisplayName("Проверим сообщение при отправке заявки в друзья")
  @ApiLogin(
      user = @TestUser(generateRandom = true,
          partners = {
              @WithPartners(status = FriendStatus.NOT_FRIEND)
          }))
  void inviteSentMsgShouldBeVisibleTest(@People Map<User, FriendStatus> people) {
    fillPeopleLists(people);
    new FriendsPage()
        .goToAllPeople()
        .clickAction()
        .checkMessage(SuccessMsg.INVITATION_SENT);
  }

  @Test
  @DisplayName("Проверим отображение статуса исходящих заявок")
  @ApiLogin(
      user = @TestUser(generateRandom = true,
          partners = {
              @WithPartners(status = FriendStatus.INVITATION_SENT)
          }))
  void statusAwaitOnOutcomeShouldBeVisibleTest(@People Map<User, FriendStatus> people) {
    fillPeopleLists(people);
    new FriendsPage()
        .goToOutcomeInvitations()
        .checkWaitingAction();
  }

  @Test
  @DisplayName("Проверим сообщения принятой заявки")
  @ApiLogin(
      user = @TestUser(generateRandom = true,
          partners = {
              @WithPartners(status = FriendStatus.INVITATION_RECEIVED)
          }))
  void acceptMsgOnInvomeShouldBeVisibleTest(@People Map<User, FriendStatus> people) {
    fillPeopleLists(people);
    new FriendsPage()
        .goToIncomeInvitations()
        .clickAcceptAction()
        .checkMessage(INVITATION_ACCEPTED);
  }

  @Test
  @DisplayName("Проверим сообщения отклоненной заявки")
  @ApiLogin(
      user = @TestUser(generateRandom = true,
          partners = {
              @WithPartners(status = FriendStatus.INVITATION_RECEIVED)
          }))
  void declineMsgOnInvomeShouldBeVisibleTest(@People Map<User, FriendStatus> people) {
    fillPeopleLists(people);
    new FriendsPage()
        .goToIncomeInvitations()
        .clickDeclineAction()
        .checkMessage(INVITATION_DECLINED);
  }

  @Test
  @DisplayName("Проверим поиск подстроки")
  @ApiLogin(
      user = @TestUser(generateRandom = true,
          partners = {
              @WithPartners(status = FriendStatus.FRIEND)
          }))
  void searchQueryShouldFilterUsersTest(@People Map<User, FriendStatus> people) {
    fillPeopleLists(people);
    new FriendsPage()
        .goToFriends()
        .inputSearchQuery(friends.get(0).username())
        .checkCountRowInTable(friends.size())
        .checkUsername(friends.get(0).username());
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
