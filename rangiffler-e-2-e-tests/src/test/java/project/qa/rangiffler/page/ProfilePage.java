package project.qa.rangiffler.page;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

public class ProfilePage extends BasePage<ProfilePage> {

  public static final String URL = CFG.frontUrl() + "/profile";

  private final SelenideElement userName = $(".avatar-container figcaption");
  private final SelenideElement avatar = $(".profile__avatar");
  private final SelenideElement avatarInput = $("input[type='file']");
  private final SelenideElement nameInput = $("input[name='firstname']");
  private final SelenideElement surnameInput = $("input[name='surname']");
  private final SelenideElement submitButton = $("button[type='submit']");
  private final SelenideElement countryCombobox = $("#location");
  private final ElementsCollection comboboxElements = $$("ul li");


  @Step("Заполнима порфиль: name: {0}, surname: {1}")
  public ProfilePage fillProfile(String name, String surname) {
    setName(name);
    setSurname(surname);
    submitProfile();
    return this;
  }

  @Step("Установим name: {0}")
  public ProfilePage setName(String name) {
    nameInput.setValue(name);
    return this;
  }

  @Step("Установим surname: {0}")
  public ProfilePage setSurname(String surname) {
    surnameInput.setValue(surname);
    return this;
  }

  @Step("Установим аватар: {0}")
  public ProfilePage setAvatar(String photoInClasspath) {
    avatarInput.uploadFromClasspath(photoInClasspath);
    return this;
  }

  @Step("Добавим страну: {0}")
  public ProfilePage setCountryCode(String countryCode) {
    countryCombobox.click();
    comboboxElements
        .filterBy(Condition.attribute("data-value", countryCode))
        .get(0)
        .click();
    return this;
  }

  @Step("Проверим username: {0}")
  public ProfilePage checkUsername(String username) {
    this.userName.should(text(username));
    return this;
  }

  @Step("Проверим country: {0}")
  public ProfilePage checkCountry(String country) {
    this.countryCombobox.should(text(country));
    return this;
  }

  @Step("Проверим name: {0}")
  public ProfilePage checkName(String name) {
    nameInput.shouldHave(value(name));
    return this;
  }

  @Step("Проверим surname: {0}")
  public ProfilePage checkSurname(String surname) {
    surnameInput.shouldHave(value(surname));
    return this;
  }

  @Step("Сохранить профиль")
  public ProfilePage submitProfile() {
    submitButton.click();
    return this;
  }

  @Step("Проверяем, что страница загружена")
  @Override
  public ProfilePage waitForPageLoaded() {
    userName.should(visible);
    return this;
  }
}