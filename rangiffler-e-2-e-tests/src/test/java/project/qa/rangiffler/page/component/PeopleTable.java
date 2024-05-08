package project.qa.rangiffler.page.component;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

public class PeopleTable extends BaseComponent <Header>{

  public PeopleTable(SelenideElement self) {
    super(self);
  }

  public ElementsCollection getAllRows() {
    return $$("tbody tr");
  }

  public SelenideElement getRowByUsername(String username) {
    ElementsCollection allRows = getAllRows();
    return allRows.find(text(username));
  }

  public SelenideElement firstRowAction(){
    return getActionsCell(getAllRows().first()).$("[type='button']");
  }

  public SelenideElement firstRowActionAccept(){
    return getActionsCell(getAllRows().first()).$$("[type='button']").find(text("Accept"));
  }

  public SelenideElement firstRowActionDecline(){
    return getActionsCell(getAllRows().first()).$$("[type='button']").find(text("Decline"));
  }

  public SelenideElement firstRowMessage(){
    return getActionsCell(getAllRows().first());
  }

  public SelenideElement getUsernameCell(SelenideElement row) {
    return row.$$("td").get(1);
  }

  public SelenideElement getFlagCell(SelenideElement row) {
    return row.$$("td").get(3);
  }

  public SelenideElement getActionsCell(SelenideElement row) {
    return row.$$("td").get(4);
  }

}
