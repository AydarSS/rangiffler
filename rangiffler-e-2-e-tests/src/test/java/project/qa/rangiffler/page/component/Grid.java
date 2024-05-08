package project.qa.rangiffler.page.component;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

public class Grid extends BaseComponent<Grid> {

  private final SelenideElement gridItem = $(".MuiGrid-root.MuiGrid-item");
  private final ElementsCollection photoItems = $$(
      ".MuiPaper-root.MuiPaper-elevation.photo-card__container");

  public Grid() {
    super($(".MuiGrid-root.MuiGrid-container"));
  }

  @Step("Получим элемент photoItem по индексу {}")
  public PhotoContainer getPhotoItem(int index) {
    return new PhotoContainer(photoItems.get(index));
  }
}
