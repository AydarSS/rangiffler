package project.qa.rangiffler.test.grpc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import guru.qa.grpc.rangiffler.PhotoOuterClass;
import guru.qa.grpc.rangiffler.PhotoOuterClass.AddPhotoRequest;
import guru.qa.grpc.rangiffler.PhotoOuterClass.ChangeLikeRequest;
import guru.qa.grpc.rangiffler.PhotoOuterClass.ChangePhotoRequest;
import guru.qa.grpc.rangiffler.PhotoOuterClass.DeletePhotoRequest;
import guru.qa.grpc.rangiffler.PhotoOuterClass.DeletePhotoResponse;
import guru.qa.grpc.rangiffler.PhotoOuterClass.GetLikesRequest;
import guru.qa.grpc.rangiffler.PhotoOuterClass.GetLikesResponse;
import guru.qa.grpc.rangiffler.PhotoOuterClass.GetPhotoByIdRequest;
import guru.qa.grpc.rangiffler.PhotoOuterClass.GetPhotosRequest;
import guru.qa.grpc.rangiffler.PhotoOuterClass.GetPhotosResponse;
import guru.qa.grpc.rangiffler.PhotoOuterClass.GetStatRequest;
import guru.qa.grpc.rangiffler.PhotoOuterClass.GetStatResponse;
import guru.qa.grpc.rangiffler.PhotoOuterClass.Paging;
import guru.qa.grpc.rangiffler.PhotoOuterClass.Photo;
import guru.qa.grpc.rangiffler.PhotoOuterClass.PhotoResponse;
import guru.qa.grpc.rangiffler.PhotoOuterClass.Statistic;
import guru.qa.grpc.rangiffler.PhotoOuterClass.Username;
import io.qameta.allure.Step;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import project.qa.rangiffler.annotation.PhotoFileConverter;
import project.qa.rangiffler.annotation.PhotoId;
import project.qa.rangiffler.annotation.WithLike;
import project.qa.rangiffler.annotation.WithManyPhoto;
import project.qa.rangiffler.annotation.WithPhoto;
import project.qa.rangiffler.db.DataSourceProvider;
import project.qa.rangiffler.db.Database;
import project.qa.rangiffler.extension.GrpcAddPhotoExtension;
import project.qa.rangiffler.stub.PhotoServiceStub;

@ExtendWith(GrpcAddPhotoExtension.class)
public class PhotoServiceGrpcTest {

  private final NamedParameterJdbcOperations photoJdbc = new NamedParameterJdbcTemplate(
      DataSourceProvider.INSTANCE.dataSource(Database.PHOTO));

  @DisplayName("Добавление фото")
  @Test
  void addPhotoTest(@PhotoFileConverter("images/usa.png") String src) {
    AddPhotoRequest request = AddPhotoRequest.newBuilder()
        .setUsername("duck")
        .setCountryCode("ru")
        .setSrc(src)
        .setDescription("hello")
        .build();

    PhotoResponse response = PhotoServiceStub.stub.addPhoto(request);
    assertNotNull(response.getId(), "Id фото не null");
  }

  @DisplayName("Изменение фото")
  @Test
  @WithManyPhoto({
      @WithPhoto(filename = "images/usa.png",
          username = "rabbit",
          countryCode = "ru",
          description = "Old Description",
          enterInMethod = true)
  })
  void changePhotoTest(@PhotoId String photoId) {
    final String changingDescription = "New description";
    final String changedCountryCode = "us";

    ChangePhotoRequest request = ChangePhotoRequest.newBuilder()
        .setId(photoId)
        .setCountryCode(changedCountryCode)
        .setDescription(changingDescription)
        .build();
    PhotoResponse response = PhotoServiceStub.stub.changePhoto(request);

    assertAll("Проверим изменение фото",
        () -> assertEquals(changingDescription, response.getDescription(), "Description изменено"),
        () -> assertEquals(changedCountryCode, response.getCountryCode(), "CountryCode изменено")
    );
  }

  @DisplayName("Удаление фото")
  @Test
  @WithManyPhoto({
      @WithPhoto(
          filename = "images/usa.png",
          username = "rabbit",
          countryCode = "ru",
          description = "Old Description",
          enterInMethod = true)
  })
  void deletePhotoTest(@PhotoId String photoId) {
    DeletePhotoRequest request = DeletePhotoRequest.newBuilder()
        .setId(photoId)
        .build();
    DeletePhotoResponse response = PhotoServiceStub.stub.deletePhoto(request);

    assertTrue(response.getIsSuccess(), "Удаление успешно");
  }

  @DisplayName("Получение фото по одному юзеру")
  @Test
  @WithManyPhoto({
      @WithPhoto(filename = "images/usa.png",
          username = "rabbit",
          countryCode = "us",
          description = "rabbit Description"),
      @WithPhoto(filename = "images/usa.png",
          username = "duck",
          countryCode = "ru",
          description = "duck Description",
          enterInMethod = true)
  })
  void getPhotoTest(@PhotoId String photoId) {
    String username = "rabbit";
    List<Username> usernamesProto = List.of(Username.newBuilder().setUsername(username).build());

    GetPhotosRequest request = GetPhotosRequest.newBuilder()
        .addAllUsername(usernamesProto)
        .setPageInfo(Paging.newBuilder()
            .setSize(10)
            .setPage(0)
            .build())
        .build();

    GetPhotosResponse response = PhotoServiceStub.stub.getPhotos(request);

    List<PhotoOuterClass.Photo> listPhoto = response.getPhotosList();
    List<String> names = listPhoto.stream().map(Photo::getUsername).toList();

    assertAll(String.format("Проверим, что есть фото созданные только %s", username),
        () -> assertThat(names).containsOnly(username),
        () -> assertThat(listPhoto).extracting(Photo::getId).doesNotContain(photoId));
  }

  @DisplayName("Получение фото по нескольким юзерам")
  @Test
  @WithManyPhoto({
      @WithPhoto(filename = "images/usa.png",
          username = "bee",
          countryCode = "us",
          description = "rabbit Description",
          enterInMethod = true),
      @WithPhoto(filename = "images/usa.png",
          username = "rabbit",
          countryCode = "us",
          description = "rabbit Description"),
      @WithPhoto(filename = "images/usa.png",
          username = "duck",
          countryCode = "ru",
          description = "duck Description")
  })
  void getPhotoWithManyUserTest(@PhotoId String photoId) {
    String usernameFirst = "rabbit";
    String usernameSecond = "duck";
    List<Username> usernamesProto = List.of(
        Username.newBuilder().setUsername(usernameFirst).build(),
        Username.newBuilder().setUsername(usernameSecond).build());

    GetPhotosRequest request = GetPhotosRequest.newBuilder()
        .addAllUsername(usernamesProto)
        .setPageInfo(Paging.newBuilder()
            .setSize(10)
            .setPage(0)
            .build())
        .build();

    GetPhotosResponse response = PhotoServiceStub.stub.getPhotos(request);

    List<PhotoOuterClass.Photo> listPhoto = response.getPhotosList();
    List<String> names = listPhoto.stream().map(Photo::getUsername).toList();

    assertAll(
        String.format("Проверим, что есть фото созданные %s и %s", usernameFirst, usernameSecond),
        () -> assertThat(names).containsOnly(usernameFirst, usernameSecond),
        () -> assertThat(listPhoto).extracting(Photo::getId).doesNotContain(photoId));
  }

  @DisplayName("Получение юзера по фото Id")
  @Test
  @WithManyPhoto({
      @WithPhoto(filename = "images/usa.png",
          username = "bee",
          countryCode = "us",
          description = "rabbit Description",
          enterInMethod = true)
  })
  void getUsernameByPhotoTest(@PhotoId String photoId) {
    String username = "bee";
    GetPhotoByIdRequest request = GetPhotoByIdRequest.newBuilder()
        .setId(photoId)
        .build();

    PhotoResponse response = PhotoServiceStub.stub.getPhoto(request);

    assertEquals(username, response.getUsername(), "Имя пользователя равно ожидаемому");
  }

  @DisplayName("Получение лайков")
  @Test
  @WithManyPhoto({
      @WithPhoto(filename = "images/usa.png",
          username = "bee",
          countryCode = "us",
          description = "rabbit Description",
          enterInMethod = true,
          likes = {
              @WithLike(username = "rabbit1", userId = "1bcbea66-85c7-4a99-be59-46c678e1a56b"),
              @WithLike(username = "duck1", userId = "1bcbea66-85c7-4a99-be59-46c678e1a57a")
          })
  })
  void getLikeTest(@PhotoId String photoId) {
    GetLikesRequest request = GetLikesRequest.newBuilder()
        .setPhotoId(photoId)
        .build();
    GetLikesResponse response = PhotoServiceStub.stub.getLikes(request);

    assertEquals(2,response.getLikesList().size(), "Количество лайков равно ожидаемому");

  }

  @DisplayName("Удаление ранее поставленного лайка")
  @Test
  @WithManyPhoto({
      @WithPhoto(filename = "images/usa.png",
          username = "bee",
          countryCode = "us",
          description = "rabbit Description",
          enterInMethod = true,
          likes = {
              @WithLike(username = "bee", userId = "1bcbea66-85c7-4a99-be59-46c678e1a56b"),
          })
  })
  void deleteLikeTest(@PhotoId String photoId) {
    long likeCount = likeCount(photoId);

    ChangeLikeRequest request = ChangeLikeRequest.newBuilder()
        .setUsername("bee")
        .setPhotoId(photoId)
        .setUserId("1bcbea66-85c7-4a99-be59-46c678e1a56b")
        .build();

    PhotoServiceStub.stub.changeLike(request);

    GetLikesRequest getLikeRequest = GetLikesRequest.newBuilder()
        .setPhotoId(photoId)
        .build();
    GetLikesResponse getLikeresponse = PhotoServiceStub.stub.getLikes(getLikeRequest);

    assertEquals(likeCount-1, getLikeresponse.getLikesList().size(), "Лайки отсутствуют");

  }

  @DisplayName("Получение статистики")
  @Test
  @WithManyPhoto({
      @WithPhoto(filename = "images/usa.png",
          username = "bee",
          countryCode = "TestForStatistic",
          description = "rabbit Description",
          enterInMethod = true),
      @WithPhoto(filename = "images/usa.png",
          username = "rabbit",
          countryCode = "TestForStatistic",
          description = "rabbit Description"),
      @WithPhoto(filename = "images/usa.png",
          username = "duck",
          countryCode = "TestForStatistic",
          description = "duck Description")
  })
  void getStatisticTest() {
    String usernameFirst = "bee";
    String usernameSecond = "duck";
    List<Username> usernamesProto = List.of(
        Username.newBuilder().setUsername(usernameFirst).build(),
        Username.newBuilder().setUsername(usernameSecond).build());

    GetStatRequest request = GetStatRequest.newBuilder()
        .addAllUsername(usernamesProto)
        .build();

    GetStatResponse response = PhotoServiceStub.stub.getStat(request);

    List<Statistic> statistic = response.getStatisticList().stream()
        .filter(stat->stat.getCountryCode().equals("TestForStatistic"))
        .toList();

    assertAll(
        String.format("Проверим, что есть статистика для %s и %s", usernameFirst, usernameSecond),
        () -> assertThat(statistic).hasSize(1));
  }

  @Step("Проверим количество лайков")
  private long likeCount(String photoId) {
    String sql = String.format("""
        SELECT count(*) as count
        FROM photo_likes
        WHERE photo_id = UUID_TO_BIN('%s')
        """, photoId);
    return (Long) photoJdbc.queryForList(sql,new HashMap<>()).get(0).get("count");
  }

}
