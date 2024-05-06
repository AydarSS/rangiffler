package project.qa.rangiffler.api;

import java.io.IOException;
import project.qa.rangiffler.extension.AddUsersExtension;
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

/*  public void doLogin(ExtensionContext context, String username, String password) throws Exception {
    authApi.authorize(
        "code",
        "client",
        "openid",
        CFG.frontUrl() + "/authorized",
        MyApiLoginExtension.getCodChallenge(context),
        "S256"
    ).execute();

    authApi.login(
        username,
        password,
        MyApiLoginExtension.getCsrfToken()
    ).execute();

    JsonNode responseBody = authApi.token(
        "Basic " + new String(
            Base64.getEncoder().encode("client:secret".getBytes(StandardCharsets.UTF_8))),
        "client",
        "http://127.0.0.1:3000/authorized",
        "authorization_code",
        MyApiLoginExtension.getCode(context),
        MyApiLoginExtension.getCodeVerifier(context)
    ).execute().body();

    final String token = responseBody.get("id_token").asText();
    MyApiLoginExtension.setToken(context, token);
  }*/


  public void doRegister(String username, String password) {
    try {
      authApi.getRegister().execute();
      final String token = AddUsersExtension.forXsrfToken.get();
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
