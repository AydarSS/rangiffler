package project.qa.rangiffler.test.web;

import static project.qa.rangiffler.page.message.SuccessMsg.PROFILE_UPDATED;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.junit5.AllureJunit5;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import project.qa.rangiffler.annotation.ApiLogin;
import project.qa.rangiffler.annotation.TestUser;
import project.qa.rangiffler.extension.ApiLoginExtension;
import project.qa.rangiffler.extension.BrowserExtension;
import project.qa.rangiffler.extension.ContextHolderExtension;
import project.qa.rangiffler.extension.GrpcAddUsersExtension;
import project.qa.rangiffler.page.MyTravelsPage;

@ExtendWith({ContextHolderExtension.class, AllureJunit5.class, BrowserExtension.class,
    GrpcAddUsersExtension.class, ApiLoginExtension.class})
public class ProfileTest {

  @Test
  @DisplayName("Проверим изменение профиля")
  @ApiLogin(user = @TestUser(generateRandom = true))
  void addPhotoTest() {
    Selenide.open(MyTravelsPage.URL, MyTravelsPage.class);
    new MyTravelsPage()
        .waitForPageLoaded()
        .toProfilePage()
        .fillProfile("Ivan", "Ivanov")
        .setAvatar("images/profilePhoto.png")
        .setCountryCode("ru")
        .submitProfile()
        .checkMessage(PROFILE_UPDATED)
        .checkName("Ivan")
        .checkSurname("Ivanov")
        .checkCountry("Russian Federation");

  }
}
