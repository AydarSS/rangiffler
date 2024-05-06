package project.qa.rangiffler.page.component;

import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.SelenideElement;

public class Header extends BaseComponent<Header>{

  private final SelenideElement exitButton = $("button[aria-label='Logout']");
  private final SelenideElement hamburgerButton = $("button[aria-label='open drawer']");

  public Header() {
    super($(".header"));
  }

}
