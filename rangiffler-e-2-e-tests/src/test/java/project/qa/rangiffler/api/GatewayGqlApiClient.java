package project.qa.rangiffler.api;

import io.qameta.allure.Step;
import project.qa.rangiffler.model.gql.GqlDeletePhoto;
import project.qa.rangiffler.model.gql.GqlError;
import project.qa.rangiffler.model.gql.GqlFeed;
import project.qa.rangiffler.model.gql.GqlPhoto;
import project.qa.rangiffler.model.gql.GqlRequest;
import project.qa.rangiffler.model.gql.GqlUser;

public class GatewayGqlApiClient extends RestClient {

  private final GraphQlGatewayApi graphQlGatewayApi;

  public GatewayGqlApiClient() {
    super(
        CFG.gatewayUrl()
    );
    graphQlGatewayApi = retrofit.create(GraphQlGatewayApi.class);
  }

  @Step("Отправка запроса текущего пользователя")
  public GqlUser currentUser(String bearerToken, GqlRequest request) throws Exception {
    return graphQlGatewayApi.currentUser(bearerToken, request).execute()
        .body();
  }

  @Step("Отправка запроса на обновление пользователя")
  public GqlUser updateUser(String bearerToken, GqlRequest request) throws Exception {
    return graphQlGatewayApi.updateUser(bearerToken, request).execute()
        .body();
  }

  @Step("Отправка запроса связанных лиц")
  public GqlUser getLinkedPerson(String bearerToken, GqlRequest request) throws Exception {
    return graphQlGatewayApi.getLinkedPersons(bearerToken, request).execute()
        .body();
  }

  @Step("Запрос на определение на дружбы")
  public GqlUser identityFriendship(String bearerToken, GqlRequest request) throws Exception {
    return graphQlGatewayApi.identityFriendship(bearerToken, request).execute()
        .body();
  }

  @Step("Добавление фото")
  public GqlPhoto addPhoto (String bearerToken, GqlRequest request) throws Exception {
    return graphQlGatewayApi.addPhoto(bearerToken, request).execute()
        .body();
  }

  @Step("Удаление чужого фото")
  public GqlError deletePhotoWithError (String bearerToken, GqlRequest request) throws Exception {
    return graphQlGatewayApi.deletePhotoWithError(bearerToken, request).execute().body();
  }

  @Step("Удаление фото")
  public GqlDeletePhoto deletePhoto (String bearerToken, GqlRequest request) throws Exception {
    return graphQlGatewayApi.deletePhoto(bearerToken, request).execute().body();
  }

  @Step("Получение фото и статистики")
  public GqlFeed getFeed (String bearerToken, GqlRequest request) throws Exception {
    return graphQlGatewayApi.getFeed(bearerToken, request).execute().body();
  }
}