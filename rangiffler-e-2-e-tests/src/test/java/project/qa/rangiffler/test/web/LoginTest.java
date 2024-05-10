package project.qa.rangiffler.test.web;

import com.codeborne.selenide.Selenide;
import com.github.javafaker.Faker;
import io.qameta.allure.junit5.AllureJunit5;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import project.qa.rangiffler.api.AuthApiClient;
import project.qa.rangiffler.extension.BrowserExtension;
import project.qa.rangiffler.page.MyTravelsPage;
import project.qa.rangiffler.page.WelcomePage;

@ExtendWith({AllureJunit5.class, BrowserExtension.class})
public class LoginTest {

  AuthApiClient authApi = new AuthApiClient();
  private final static String PASSWORD = "12345";

  @Test
  @DisplayName("Проверяем успешную авторизацию")
  void myTravelsPageShouldBeVisibleTest() {
    String username = new Faker().name().username();
    authApi.doRegister(username, PASSWORD);
    Selenide.open(WelcomePage.URL, WelcomePage.class)
        .login()
        .fillLoginPage(username, PASSWORD)
        .submit();
    new MyTravelsPage().waitForPageLoaded();
  }

  @Test
  @DisplayName("Неверные учетные данные (пользователь). Проверяем текст ошибки")
  void invalidCredentialsUsernameTest() {
    String username = new Faker().name().username();
    Selenide.open(WelcomePage.URL, WelcomePage.class)
        .login()
        .fillLoginPage(username, PASSWORD)
        .submit()
        .invalidCredentialsTextShouldBeVisible();
  }

  @Test
  @DisplayName("Неверные учетные данные (пароль). Проверяем текст ошибки")
  void invalidCredentialsPasswordTest() {
    String username = new Faker().name().username();
    authApi.doRegister(username, PASSWORD);
    Selenide.open(WelcomePage.URL, WelcomePage.class)
        .login()
        .fillLoginPage(username, "987654")
        .submit()
        .invalidCredentialsTextShouldBeVisible();
  }

}
