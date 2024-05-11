package project.qa.rangiffler.page;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

public class WelcomePage extends BasePage<WelcomePage> {

  public static final String URL = CFG.frontUrl();

  private final SelenideElement loginButton = $(byText("Login"));
  private final SelenideElement registerButton = $(byText("Register"));

  @Step("Redirect на логин")
  public LoginPage login() {
    loginButton.click();
    return new LoginPage();
  }

  @Step("Redirect на регистрацию")
  public RegisterPage register() {
    registerButton.click();
    return new RegisterPage();
  }

  @Override
  public WelcomePage waitForPageLoaded() {
    return null;
  }
}
