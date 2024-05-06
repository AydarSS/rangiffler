package project.qa.rangiffler.test.web;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import project.qa.rangiffler.page.MyTravelsPage;
import project.qa.rangiffler.page.WelcomePage;

public class LoginTest {

  @Test
  @DisplayName("Проверяем успешную авторизацию")
  void myTravelsPageShouldBeVisible() {
    Selenide.open(WelcomePage.URL, WelcomePage.class)
        .login()
        .fillLoginPage("rabbit", "12345")
        .submit();
    new MyTravelsPage().waitForPageLoaded();
  }

  @Test
  @DisplayName("Неверные учетные данные. Проверяем текст ошибки")
  void invalidCredentials() {
    Selenide.open(WelcomePage.URL, WelcomePage.class)
        .login()
        .fillLoginPage("rabbit1", "12345")
        .submit()
        .invalidCredentialsTextShouldBeVisible();
  }

}
