package project.qa.rangiffler.page.component;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import project.qa.rangiffler.page.EditPhoto;

public class PhotoContainer extends BaseComponent<PhotoContainer>{

  private final SelenideElement likeButton = $("button[aria-label='like']");
  private final SelenideElement photoDescription = $(".photo-card__content");
  private final SelenideElement editPhoto = $(byText("Edit"));
  private final SelenideElement deletePhoto = $(byText("Delete"));


  public PhotoContainer(SelenideElement self) {
    super(self);
  }

  public ElementsCollection getAllPhotos() {
    return $$(".MuiGrid-root .MuiGrid-item");
  }

  public PhotoContainer deletePhoto(){
    deletePhoto.click();
    return this;
  }

  public EditPhoto editPhoto(){
    editPhoto.click();
    return new EditPhoto();
  }

  public PhotoContainer checkPhotoHasDescription(String description){
    photoDescription.shouldHave(text(description));
    return this;
  }

}
