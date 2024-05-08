package project.qa.rangiffler.test.web;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.junit5.AllureJunit5;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import project.qa.rangiffler.annotation.AddedPhotos;
import project.qa.rangiffler.annotation.ApiLogin;
import project.qa.rangiffler.annotation.LikeInfo;
import project.qa.rangiffler.annotation.TestUser;
import project.qa.rangiffler.annotation.WithLike;
import project.qa.rangiffler.annotation.WithPartners;
import project.qa.rangiffler.extension.ApiLoginExtension;
import project.qa.rangiffler.extension.BrowserExtension;
import project.qa.rangiffler.extension.ContextHolderExtension;
import project.qa.rangiffler.extension.GrpcAddUsersExtension;
import project.qa.rangiffler.model.FriendStatus;
import project.qa.rangiffler.page.MyTravelsPage;
import project.qa.rangiffler.page.message.ErrorMsg;
import project.qa.rangiffler.page.message.SuccessMsg;

@ExtendWith({ContextHolderExtension.class, AllureJunit5.class, BrowserExtension.class,
    GrpcAddUsersExtension.class, ApiLoginExtension.class})
public class MyTravelsTest {

  @BeforeEach()
  void openUrl() {
    Selenide.open(MyTravelsPage.URL);
  }

  @Test
  @DisplayName("Проверим добавление фото")
  @ApiLogin(user = @TestUser(generateRandom = true))
  void addPhotoTest() {

    new MyTravelsPage()
        .waitForPageLoaded()
        .addPhoto()
        .uploadPhoto("images/usa.png")
        .setCountryCode("tg")
        .setDescription("Description")
        .save();

    new MyTravelsPage()
        .checkMessage(SuccessMsg.POST_CREATED)
        .checkPhotosCount(1);
  }

  @Test
  @DisplayName("Проверим отображение фото с друзьями")
  @ApiLogin(
      user = @TestUser(generateRandom = true,
          partners = {
              @WithPartners(status = FriendStatus.FRIEND,
                  photos = {@AddedPhotos(filename = "images/egypt.png",
                      likes = @WithLike
                          (likeInfo = @LikeInfo
                              (withLikeAuthorizedUser = true,
                                  withLCreatedPhotoUser = true))),
                      @AddedPhotos(filename = "images/turkey.png"),
                  }),
              @WithPartners(status = FriendStatus.NOT_FRIEND,
                  photos = {@AddedPhotos(filename = "images/netherlands.png")
                  })
          }
      ))
  void photoWithFriendsShouldBeVisibleTest() {
    new MyTravelsPage()
        .waitForPageLoaded()
        .photosWithFriends()
        .checkPhotosCount(2);
  }

  @Test
  @DisplayName("Проверим отображение лайков")
  @ApiLogin(
      user = @TestUser(generateRandom = true,
          partners = {
              @WithPartners(status = FriendStatus.FRIEND,
                  photos = {@AddedPhotos(filename = "images/egypt.png",
                      likes = @WithLike
                          (likeInfo = @LikeInfo
                              (withLikeAuthorizedUser = true,
                                  withLCreatedPhotoUser = true)))
                  })
          }
      ))
  void likesOnPhotoWithFriendsShouldBeVisibleTest() {
    new MyTravelsPage()
        .waitForPageLoaded()
        .photosWithFriends()
        .checkCountLikesOnFirstPhotoShouldBe(2);
  }

  @Test
  @DisplayName("Проверим закрашивание лайка")
  @ApiLogin(
      user = @TestUser(generateRandom = true,
          partners = {
              @WithPartners(status = FriendStatus.FRIEND,
                  photos = {@AddedPhotos(filename = "images/egypt.png",
                      likes = @WithLike
                          (likeInfo = @LikeInfo
                              (withLikeAuthorizedUser = true,
                                  withLCreatedPhotoUser = true)))
                  })
          }
      ))
  void likeItemPaintOverShouldBeVisibleTest() {
    new MyTravelsPage()
        .waitForPageLoaded()
        .photosWithFriends()
        .checklikeItemMustBePaintOver();
  }

  @Test
  @DisplayName("Проверим отображения фото пользователя")
  @ApiLogin(
      user = @TestUser(generateRandom = true,
          photos = {@AddedPhotos(filename = "images/egypt.png")})
  )
  void photoShouldBeVisibleTest() {
    new MyTravelsPage()
        .waitForPageLoaded()
        .checklikeItemMustBePaintOver();
  }

  @Test
  @DisplayName("Проверим удаление фото")
  @ApiLogin(
      user = @TestUser(generateRandom = true,
          photos = {@AddedPhotos(filename = "images/egypt.png")})
  )
  void deletePhotoTest() {
    new MyTravelsPage()
        .waitForPageLoaded()
        .deletePhoto()
        .checkPhotosCount(0);
  }

  @Test
  @DisplayName("Проверим редактирование фото")
  @ApiLogin(
      user = @TestUser(generateRandom = true,
          photos = {@AddedPhotos(filename = "images/egypt.png")})
  )
  void editPhotoTest() {
    new MyTravelsPage()
        .waitForPageLoaded()
        .editPhoto()
        .setCountryCode("eg")
        .setDescription("Hello")
        .save();

    new MyTravelsPage()
        .checkMessage(SuccessMsg.POST_UPDATED);
  }

  @Test
  @DisplayName("Проверим, что не можем удалить фото друга")
  @ApiLogin(
      user = @TestUser(generateRandom = true,
          partners = {
              @WithPartners(status = FriendStatus.FRIEND,
                  photos = {@AddedPhotos(filename = "images/egypt.png",
                      likes = @WithLike
                          (likeInfo = @LikeInfo
                              (withLikeAuthorizedUser = true,
                                  withLCreatedPhotoUser = true)))
                  })
          }
      ))
  void deleteFriendPhotoTest() {
    new MyTravelsPage()
        .waitForPageLoaded()
        .photosWithFriends()
        .deletePhoto()
        .checkMessage(ErrorMsg.CAN_NOT_DELETE_POST)
        .checkPhotosCount(1);
  }

  @Test
  @DisplayName("Проверим, что не можем редактировать фото друга")
  @ApiLogin(
      user = @TestUser(generateRandom = true,
          partners = {
              @WithPartners(status = FriendStatus.FRIEND,
                  photos = {@AddedPhotos(filename = "images/egypt.png",
                      likes = @WithLike
                          (likeInfo = @LikeInfo
                              (withLikeAuthorizedUser = true,
                                  withLCreatedPhotoUser = true)))
                  })
          }
      ))
  void editFriendPhotoTest() {
    new MyTravelsPage()
        .waitForPageLoaded()
        .photosWithFriends()
        .editPhoto()
        .setCountryCode("eg")
        .setDescription("Hello")
        .save();

    new MyTravelsPage()
        .checkMessage(ErrorMsg.CAN_NOT_UPDATE_POST);
  }


}
