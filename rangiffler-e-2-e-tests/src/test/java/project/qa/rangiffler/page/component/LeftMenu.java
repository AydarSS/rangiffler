package project.qa.rangiffler.page.component;

import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.SelenideElement;

public class LeftMenu extends BaseComponent<LeftMenu> {

  private final SelenideElement profileButton = $("a[href*='/profile");
  private final SelenideElement travelsButton = $("a[href*='/my-travels");
  private final SelenideElement friendsButton = $("a[href*='/people");

  public LeftMenu() {
    super($("ul.MuiList-root.MuiList-padding"));
  }


}
