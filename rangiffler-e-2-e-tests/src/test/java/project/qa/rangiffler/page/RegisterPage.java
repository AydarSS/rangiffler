package project.qa.rangiffler.page;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import project.qa.rangiffler.page.message.ErrorMsg;

public class RegisterPage extends BasePage<RegisterPage> {

  public static final String URL = CFG.authUrl() + "/register";

  private final SelenideElement usernameInput = $("input[name='username']");
  private final SelenideElement passwordInput = $("input[name='password']");
  private final SelenideElement passwordSubmitInput = $("input[name='passwordSubmit']");
  private final SelenideElement submitButton = $("button[type='submit']");
  private final SelenideElement proceedLoginLink = $("a[href*='redirect']");
  private final SelenideElement errorContainer = $(".form__error");

  @Step("Fill register page with credentials: username: {0}, password: {1}, submit password: {2}")
  public RegisterPage fillRegisterPage(String login, String password, String passwordSubmit) {
    setUsername(login);
    setPassword(password);
    setPasswordSubmit(passwordSubmit);
    return this;
  }

  @Step("Вводим username: {0}")
  public RegisterPage setUsername(String username) {
    usernameInput.setValue(username);
    return this;
  }

  @Step("Вводим password: {0}")
  public RegisterPage setPassword(String password) {
    passwordInput.setValue(password);
    return this;
  }

  @Step("Подтверждение password: {0}")
  public RegisterPage setPasswordSubmit(String password) {
    passwordSubmitInput.setValue(password);
    return this;
  }

  @Step("Кликаем по регистрации")
  public LoginPage successSubmit() {
    submitButton.click();
    proceedLoginLink.click();
    return new LoginPage();
  }

  @Step("Ошибка при клике на регистрацию")
  public RegisterPage errorSubmit() {
    submitButton.click();
    return this;
  }

  @Step("Проверяем, что страница загружена")
  @Override
  public RegisterPage waitForPageLoaded() {
    usernameInput.should(visible);
    passwordInput.should(visible);
    passwordSubmitInput.should(visible);
    return this;
  }

  public RegisterPage checkErrorMessage(ErrorMsg errorMessage) {
    errorContainer.shouldHave(text(errorMessage.getMessage()));
    return this;
  }

}
