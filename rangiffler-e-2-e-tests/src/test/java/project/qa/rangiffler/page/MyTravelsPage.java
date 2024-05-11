package project.qa.rangiffler.page;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import project.qa.rangiffler.page.component.PhotoContainer;

public class MyTravelsPage extends BasePage<MyTravelsPage> {

  public static final String URL = CFG.frontUrl() + "/my-travels";

  private final SelenideElement addPhotoButton = $(byText("Add photo"));
  private final SelenideElement myTravelsButton = $("button[value='my']");
  private final SelenideElement withFriendsButton = $("button[value='friends']");
  private final PhotoContainer photos = new PhotoContainer($("MuiGrid-root.MuiGrid-container.MuiGrid-spacing-xs-3 "));
  private final SelenideElement profileButton = $("a[href*='/profile']");

  @Step("Проверим что страница загрузилась")
  @Override
  public MyTravelsPage waitForPageLoaded() {
    addPhotoButton.should(visible);
    myTravelsButton.should(visible);
    return this;
  }

  @Step("Кликаем на добавить фото")
  public EditPhoto addPhoto() {
    addPhotoButton.click();
    return new EditPhoto();
  }

  @Step("Кликаем на кнопку фото с друзьями")
  public MyTravelsPage photosWithFriends() {
    withFriendsButton.click();
    return this;
  }

  @Step("Проверяем количество стран на странице, должно быть {count}")
  public void checkPhotosCount(int count) {
    photos.getAllPhotos().shouldHave(CollectionCondition.size(count));
  }

  @Step("Проверяем количество лайков на первом фото {count}")
  public void checkCountLikesOnFirstPhotoShouldBe(int count) {
    photos.getAllPhotos()
        .first()
        .$(".MuiTypography-root")
        .shouldHave(text(count + " likes"));
  }

  @Step("Проверим, что иконка лайка закрашена")
  public void checklikeItemMustBePaintOver() {
    photos.getAllPhotos()
        .first()
        .$(".MuiSvgIcon-root[data-testid='FavoriteOutlinedIcon']")
        .shouldBe(visible);
  }

  @Step("Удаляем фото")
  public MyTravelsPage deletePhoto() {
    photos.deletePhoto();
    return this;
  }

  @Step("Редактируем")
  public EditPhoto editPhoto() {
    return photos.editPhoto();
  }

  public ProfilePage toProfilePage(){
    profileButton.click();
    return new ProfilePage();
  }

}
