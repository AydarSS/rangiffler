package project.qa.rangiffler.page;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

public class LoginPage extends BasePage<LoginPage>{

  public static final String URL = CFG.authUrl() + "/login";

  private final SelenideElement usernameInput = $("input[name='username']");
  private final SelenideElement passwordInput = $("input[name='password']");
  private final SelenideElement submitButton = $("button[type='submit']");
  private final SelenideElement signUpButton = $("a[href*='/register");
  private final SelenideElement invalidCredentials = $(".form__error");


  @Step("Fill login page with credentials: username: {0}, password: {1}")
  public LoginPage  fillLoginPage(String login, String password) {
    setUsername(login);
    setPassword(password);
    return this;
  }

  @Step("Invalid Credentials text should be visible")
  public void invalidCredentialsTextShouldBeVisible(){
    invalidCredentials.shouldBe(exactText("Неверные учетные данные пользователя"));
  }

  @Step("Set username: {0}")
  public LoginPage setUsername(String username) {
    usernameInput.setValue(username);
    return this;
  }

  @Step("Set password: {0}")
  public LoginPage setPassword(String password) {
    passwordInput.setValue(password);
    return this;
  }

  @Step("Submit login")
  public LoginPage submit() {
    submitButton.click();
    return this;
  }

  @Step("Click signUp")
  public void signUp() {
    signUpButton.click();
  }

  @Step("Check that page is loaded")
  @Override
  public LoginPage waitForPageLoaded() {
    usernameInput.should(visible);
    passwordInput.should(visible);
    return this;
  }

}
