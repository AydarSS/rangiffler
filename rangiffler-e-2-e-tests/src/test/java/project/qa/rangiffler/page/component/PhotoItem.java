package project.qa.rangiffler.page.component;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.SelenideElement;

public class PhotoItem extends BaseComponent<PhotoItem>{


  private final SelenideElement likeButton = $("button[aria-label='like']");
  private final SelenideElement photoDescription = $(".photo-card__content");
  private final SelenideElement editPhoto = $(byText("Edit"));
  private final SelenideElement deletePhoto = $(byText("Delete"));

  public PhotoItem(SelenideElement self) {
    super(self);
  }
}
