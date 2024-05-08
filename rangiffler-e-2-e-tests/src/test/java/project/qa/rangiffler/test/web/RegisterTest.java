package project.qa.rangiffler.test.web;

import static project.qa.rangiffler.page.message.ErrorMsg.PASSWORD_IS_SHORT;
import static project.qa.rangiffler.page.message.ErrorMsg.PASSWORD_SHOULD_BE_EQUAL;

import com.codeborne.selenide.Selenide;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import project.qa.rangiffler.page.RegisterPage;

public class RegisterTest {

  private final String password = "12345";

  @Test
  @DisplayName("Успешная регистрация")
  void registerSuccessTest() {
    String username = new Faker().name().username();
    Selenide.open(RegisterPage.URL, RegisterPage.class)
        .fillRegisterPage(username,
            password,
            password)
        .successSubmit()
        .waitForPageLoaded();
  }

  @Test
  @DisplayName("Пароль и подтверждение пароля не совпадают")
  void invalidCredentialsPasswordTest() {
    String username = new Faker().name().username();
    String anotherPassword = "987654";
    Selenide.open(RegisterPage.URL, RegisterPage.class)
        .fillRegisterPage(username,
            password,
            anotherPassword)
        .errorSubmit()
        .checkErrorMessage(PASSWORD_SHOULD_BE_EQUAL);
  }

  @Test
  @DisplayName("Пароль меньше 3 символов")
  void passwordShortTest() {
    String username = new Faker().name().username();
    String shortPassword = "92";
    Selenide.open(RegisterPage.URL, RegisterPage.class)
        .fillRegisterPage(username,
            shortPassword,
            shortPassword)
        .errorSubmit()
        .checkErrorMessage(PASSWORD_IS_SHORT);
  }

}
