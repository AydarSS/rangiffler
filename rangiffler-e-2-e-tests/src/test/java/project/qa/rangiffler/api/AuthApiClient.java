package project.qa.rangiffler.api;

import com.fasterxml.jackson.databind.JsonNode;
import io.qameta.allure.Step;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.junit.jupiter.api.extension.ExtensionContext;
import project.qa.rangiffler.extension.AddUsersExtension;
import project.qa.rangiffler.extension.ApiLoginExtension;
import project.qa.rangiffler.interceptor.CodeInterceptor;

public class AuthApiClient extends RestClient {

  private final AuthApi authApi;

  public AuthApiClient() {
    super(
        CFG.authUrl(),
        true,
        new CodeInterceptor()
    );
    authApi = retrofit.create(AuthApi.class);
  }

  @Step("Логинимся пользователем {username}")
  public void doLogin(ExtensionContext context, String username, String password) throws Exception {
    authApi.authorize(
        "code",
        "client",
        "openid",
        CFG.frontUrl() + "/authorized",
        ApiLoginExtension.getCodChallenge(context),
        "S256"
    ).execute();

    login(username, password);
    token(context);
  }

  @Step("Запрос на получение токена и кладем в extension")
  private void token(ExtensionContext context) throws IOException {
    JsonNode responseBody = authApi.token(
        "Basic " + new String(Base64.getEncoder().encode("client:secret".getBytes(StandardCharsets.UTF_8))),
        "client",
        CFG.frontUrl() + "/authorized",
        "authorization_code",
        ApiLoginExtension.getCode(context),
        ApiLoginExtension.getCodeVerifier(context)
    ).execute().body();

    final String token = responseBody.get("id_token").asText();
    ApiLoginExtension.setToken(context, token);
  }

  @Step("Направляем запрос Login {username}")
  private void login(String username, String password) throws IOException {
    authApi.login(
        username,
        password,
        ApiLoginExtension.getCsrfToken()
    ).execute();
  }

  @Step("Направляем api запрос на регистрацию пользователя {username}")
  public void doRegister(String username, String password) {
    try {
      authApi.getRegister().execute();
      final String token = AddUsersExtension.xsrfTokenHolder.get();
      authApi.postRegister(
          "XSRF-TOKEN=" + token,
          username,
          password,
          password,
          token).execute();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
