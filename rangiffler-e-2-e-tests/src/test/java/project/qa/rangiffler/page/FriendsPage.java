package project.qa.rangiffler.page;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import project.qa.rangiffler.page.component.PeopleTable;

public class FriendsPage extends BasePage<FriendsPage> {

  public static final String URL = CFG.frontUrl() + "/people";

  private PeopleTable peopleTable = new PeopleTable($(".table"));
  private SelenideElement search = $("input[placeholder='Search people'");
  private final SelenideElement searchButton = $("button[type='submit']");
  private final SelenideElement friends = $(byText("Friends"));
  private final SelenideElement allPeople = $(byText("All People"));
  private final SelenideElement outcomeInvitations = $(byText("Outcome invitations"));
  private final SelenideElement incomeInvitations = $(byText("Income invitations"));


  public FriendsPage checkCountRowInTable(int count) {
    peopleTable.getAllRows().shouldHave(CollectionCondition.size(count));
    return this;
  }

  public FriendsPage clickAction() {
    peopleTable.firstRowAction().click();
    return this;
  }

  public FriendsPage clickAcceptAction() {
    peopleTable.firstRowActionAccept().click();
    return this;
  }

  public FriendsPage clickDeclineAction() {
    peopleTable.firstRowActionDecline().click();
    return this;
  }

  public FriendsPage checkWaitingAction() {
    peopleTable.firstRowMessage().shouldBe(Condition.text("Waiting..."));
    return this;
  }

  public FriendsPage goToFriends() {
    friends.click();
    return this;
  }

  public FriendsPage inputSearchQuery(String searchQuery) {
    search.setValue(searchQuery);
    searchButton.click();
    return this;
  }

  public FriendsPage goToOutcomeInvitations() {
    outcomeInvitations.click();
    return this;
  }

  public FriendsPage goToIncomeInvitations() {
    incomeInvitations.click();
    return this;
  }

  public FriendsPage goToAllPeople() {
    allPeople.click();
    return this;
  }

  public FriendsPage checkUsername(String username) {
    peopleTable.getUsernameCell(peopleTable.getRowByUsername(username)).shouldBe(Condition.visible);
    return this;
  }

  public FriendsPage checkFlag(String username, String expectedCountryName) {
    peopleTable.getFlagCell(peopleTable.getRowByUsername(username))
        .shouldBe(Condition.text(expectedCountryName));
    return this;
  }

  public FriendsPage checkRemoveAction(String username) {
    peopleTable.getActionsCell(peopleTable.getRowByUsername(username))
        .shouldBe(Condition.text("Remove"));
    return this;
  }

  public FriendsPage checkAcceptAction(String username) {
    peopleTable.getActionsCell(peopleTable.getRowByUsername(username))
        .shouldBe(Condition.text("Accept"));
    return this;
  }

  public FriendsPage checkDeclineAction(String username) {
    peopleTable.getActionsCell(peopleTable.getRowByUsername(username))
        .shouldBe(Condition.text("Decline"));
    return this;
  }

  @Override
  public FriendsPage waitForPageLoaded() {
    search.shouldBe(Condition.visible);
    return this;
  }
}
