package project.qa.rangiffler.page;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

public class MyTravelsPage extends BasePage<MyTravelsPage> {

  public static final String URL = CFG.frontUrl() + "/my-travels";

  private final SelenideElement addPhotoButton = $(byText("Add photo"));
  private final SelenideElement myTravelsButton = $("button[value='my']");
  private final SelenideElement withFriendsButton = $("button[value='friends']");

  @Step("Check that page is loaded")
  @Override
  public MyTravelsPage waitForPageLoaded() {
    addPhotoButton.should(visible);
    myTravelsButton.should(visible);
    return this;
  }
}
