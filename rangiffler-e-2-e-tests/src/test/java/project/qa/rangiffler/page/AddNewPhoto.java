package project.qa.rangiffler.page;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.SelenideElement;

public class AddNewPhoto extends BasePage<AddNewPhoto> {

  private final SelenideElement uploadPhoto = $(byText("Upload new image"));
  private final SelenideElement descriptionInput = $("#description");
  private final SelenideElement countryCombobox = $("#country");
  private final SelenideElement saveButton = $("button[type='submit']");
  private final SelenideElement closeButton = $(byText("Close"));

  @Override
  public AddNewPhoto waitForPageLoaded() {
    return null;
  }
}
