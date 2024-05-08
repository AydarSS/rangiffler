package project.qa.rangiffler.test.web;

import com.codeborne.selenide.Selenide;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import project.qa.rangiffler.annotation.ApiLogin;
import project.qa.rangiffler.annotation.TestUser;
import project.qa.rangiffler.annotation.UserParam;
import project.qa.rangiffler.api.AuthApi;
import project.qa.rangiffler.api.AuthApiClient;
import project.qa.rangiffler.model.User;
import project.qa.rangiffler.page.MyTravelsPage;
import project.qa.rangiffler.page.WelcomePage;

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
