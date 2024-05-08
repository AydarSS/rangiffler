package project.qa.rangiffler.page;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

public class EditPhoto extends BasePage<EditPhoto> {

  private final SelenideElement uploadPhoto = $(byText("Upload new image"));
  private final SelenideElement addPhotoInput = $("input[type='file']");
  private final SelenideElement descriptionInput = $("#description");
  private final SelenideElement countryCombobox = $("#country");
  private final ElementsCollection comboboxElements = $$("ul li");
  private final SelenideElement saveButton = $("button[type='submit']");
  private final SelenideElement closeButton = $(byText("Close"));

  @Override
  public EditPhoto waitForPageLoaded() {
    return null;
  }


  public void save() {
    saveButton.click();
  }

  @Step("Add photo: {0}")
  public EditPhoto uploadPhoto(String photoInClasspath) {
    addPhotoInput.uploadFromClasspath(photoInClasspath);
    return this;
  }

  @Step("Add description: {0}")
  public EditPhoto setDescription(String description) {
    descriptionInput.setValue(description);
    return this;
  }

  @Step("Add Country: {0}")
  public EditPhoto setCountryCode(String countryCode) {
    countryCombobox.click();
    comboboxElements
        .filterBy(Condition.attribute("data-value", countryCode))
        .get(0)
        .click();
    return this;
  }
}
