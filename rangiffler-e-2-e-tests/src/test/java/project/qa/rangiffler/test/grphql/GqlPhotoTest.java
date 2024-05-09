package project.qa.rangiffler.test.grphql;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import project.qa.rangiffler.annotation.AddedPhotos;
import project.qa.rangiffler.annotation.ApiLogin;
import project.qa.rangiffler.annotation.GqlRequestFile;
import project.qa.rangiffler.annotation.People;
import project.qa.rangiffler.annotation.TestUser;
import project.qa.rangiffler.annotation.Token;
import project.qa.rangiffler.annotation.UserParam;
import project.qa.rangiffler.annotation.WithPartners;
import project.qa.rangiffler.db.DataSourceProvider;
import project.qa.rangiffler.db.Database;
import project.qa.rangiffler.model.FriendStatus;
import project.qa.rangiffler.model.User;
import project.qa.rangiffler.model.gql.GqlDeletePhoto;
import project.qa.rangiffler.model.gql.GqlError;
import project.qa.rangiffler.model.gql.GqlFeed;
import project.qa.rangiffler.model.gql.GqlPhoto;
import project.qa.rangiffler.model.gql.GqlRequest;

public class GqlPhotoTest extends BaseGraphQLTest {

  private final NamedParameterJdbcOperations photoJdbc = new NamedParameterJdbcTemplate(
      DataSourceProvider.INSTANCE.dataSource(Database.PHOTO));
  private final List<User> friends = new ArrayList<>();
  private final List<User> invitationSend = new ArrayList<>();
  private final List<User> invitationsReceived = new ArrayList<>();
  private final List<User> notFriend = new ArrayList<>();

  @DisplayName("Добавление фото")
  @Test()
  @ApiLogin(
      user = @TestUser(generateRandom = true)
  )
  void addPhotoShouldBeSuccessTest(@UserParam User testUser,
      @Token String bearerToken,
      @GqlRequestFile("gql/CreatePhoto.json") GqlRequest request) throws Exception {

    final GqlPhoto response = gatewayGqlApiClient.addPhoto(bearerToken, request);
    assertNotNull(
        response.getData().getPhoto().getId()
    );
  }

  @DisplayName("Удаление чужого фото")
  @Test()
  @ApiLogin(
      user = @TestUser(generateRandom = true,
          partners = {@WithPartners(status = FriendStatus.FRIEND,
              photos = {@AddedPhotos(filename = "images/egypt.png")
              })
          }
      ))
  void deleteNotOwnPhotoShouldBeErrorTest(@Token String bearerToken,
      @People Map<User, FriendStatus> people) throws Exception {
    fillPeopleLists(people);

    String sql = String.format("""
        SELECT BIN_TO_UUID(id) as id
        FROM photo
        WHERE username = '%s'
        """, friends.get(0).username());
    String photoId = (String) photoJdbc.queryForList(sql, new HashMap<>()).get(0).get("id");

    GqlRequest request = GqlRequest.deletePhotoGqlRequest(photoId);
    final GqlError response = gatewayGqlApiClient.deletePhotoWithError(bearerToken, request);

    assertTrue(response
        .getErrors()
        .get(0)
        .getMessage()
        .contains("can not delete photo created by"), "Проверим текст сообщения");
  }

  @DisplayName("Удаление своего фото")
  @Test()
  @ApiLogin(
      user = @TestUser(generateRandom = true,
          photos = {@AddedPhotos(filename = "images/egypt.png")})
  )
  void deleteOwnPhotoShouldBeSuccessTest(
      @UserParam User testUser,
      @Token String bearerToken) throws Exception {

    String sql = String.format("""
        SELECT BIN_TO_UUID(id) as id
        FROM photo
        WHERE username = '%s'
        """, testUser.username());
    String photoId = (String) photoJdbc.queryForList(sql, new HashMap<>()).get(0).get("id");

    GqlRequest request = GqlRequest.deletePhotoGqlRequest(photoId);
    final GqlDeletePhoto response = gatewayGqlApiClient.deletePhoto(bearerToken, request);

    assertTrue(response.getData().isDeletePhoto(), "Проверим удаление сообщения");
  }

  @DisplayName("Получение своих фото")
  @Test()
  @ApiLogin(
      user = @TestUser(generateRandom = true,
          photos = {@AddedPhotos(filename = "images/egypt.png")})
  )
  void getFeedShouldBeSuccessTest(
      @UserParam User testUser,
      @Token String bearerToken,
      @GqlRequestFile("gql/GetFeed.json") GqlRequest request) throws Exception {

    final GqlFeed response = gatewayGqlApiClient.getFeed(bearerToken, request);
    assertAll(
        ()-> assertEquals(1, response.getData().getFeed().getPhotos().getEdges().size(), "Количество фото"),
        ()-> assertEquals(1, response.getData().getFeed().getStat().get(0).getCount(), "Статистика по стране")
    );
  }
  
  private void fillPeopleLists(Map<User, FriendStatus> people) {
    for (Entry<User, FriendStatus> person : people.entrySet()) {
      switch (person.getValue()) {
        case FRIEND -> friends.add(person.getKey());
        case INVITATION_RECEIVED -> invitationsReceived.add(person.getKey());
        case INVITATION_SENT -> invitationSend.add(person.getKey());
        case NOT_FRIEND -> notFriend.add(person.getKey());
      }
    }
  }
}
