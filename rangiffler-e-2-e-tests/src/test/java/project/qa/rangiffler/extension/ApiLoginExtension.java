package project.qa.rangiffler.extension;

import com.codeborne.selenide.LocalStorage;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import java.util.Objects;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;
import org.openqa.selenium.Cookie;
import project.qa.rangiffler.annotation.ApiLogin;
import project.qa.rangiffler.annotation.Token;
import project.qa.rangiffler.api.AuthApiClient;
import project.qa.rangiffler.api.cookie.ThreadSafeCookieManager;
import project.qa.rangiffler.config.Config;
import project.qa.rangiffler.model.User;
import project.qa.rangiffler.utils.OauthUtils;

public class ApiLoginExtension implements BeforeEachCallback, AfterTestExecutionCallback,
    ParameterResolver {

  private static final Config CFG = Config.getInstance();
  private final AuthApiClient authApiClient = new AuthApiClient();
  private final boolean initBrowser;

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(
      ApiLoginExtension.class);

  public ApiLoginExtension() {
    this(true);
  }

  public ApiLoginExtension(boolean initBrowser) {
    this.initBrowser = initBrowser;
  }

  @Override
  public void beforeEach(ExtensionContext extensionContext) throws Exception {
    ApiLogin apiLogin = AnnotationSupport.findAnnotation(
        extensionContext.getRequiredTestMethod(),
        ApiLogin.class).orElse(null);
    if (Objects.isNull(apiLogin)) {
      return;
    }
    User userForApiLogin = getCreatedUserForApiLogin(extensionContext);
    final String login = userForApiLogin.username();
    final String password = userForApiLogin.testData().password();
    final String codeVerifier = OauthUtils.generateCodeVerifier();
    final String codeChallenge = OauthUtils.generateCodeChallange(codeVerifier);

    setCodeVerifier(extensionContext, codeVerifier);
    setCodChallenge(extensionContext, codeChallenge);
    authApiClient.doLogin(extensionContext, login, password);

    if (initBrowser) {
      Selenide.open(CFG.frontUrl());
      LocalStorage local = Selenide.localStorage();
      local.setItem(
          "codeChallenge", getCodChallenge(extensionContext)
      );
      local.setItem(
          "id_token", getToken(extensionContext)
      );
      local.setItem(
          "codeVerifier", getCodeVerifier(extensionContext)
      );

      WebDriverRunner.getWebDriver().manage().addCookie(
          jsessionCookie()
      );
      Selenide.refresh();
    }
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext,
      ExtensionContext extensionContext) throws ParameterResolutionException {
    return
        AnnotationSupport.findAnnotation(parameterContext.getParameter(), Token.class).isPresent()
            &&
            parameterContext.getParameter().getType().isAssignableFrom(String.class);
  }

  @Override
  public String resolveParameter(ParameterContext parameterContext,
      ExtensionContext extensionContext) throws ParameterResolutionException {
    return "Bearer " + getToken(extensionContext);
  }

  @SuppressWarnings("unchecked")
  private static User getCreatedUserForApiLogin(ExtensionContext extensionContext) {
    return extensionContext
        .getStore(AddUsersExtension.ADD_USERS_NAMESPACE)
        .get(extensionContext.getUniqueId(), User.class);
  }

  @Override
  public void afterTestExecution(ExtensionContext extensionContext) throws Exception {
    ThreadSafeCookieManager.INSTANCE.removeAll();
  }

  public static void setCodeVerifier(ExtensionContext context, String codeVerifier) {
    context.getStore(ApiLoginExtension.NAMESPACE).put("code_verifier", codeVerifier);
  }

  public static void setCodChallenge(ExtensionContext context, String codeChallenge) {
    context.getStore(ApiLoginExtension.NAMESPACE).put("code_challenge", codeChallenge);
  }

  public static void setCode(ExtensionContext context, String code) {
    context.getStore(ApiLoginExtension.NAMESPACE).put("code", code);
  }

  public static void setToken(ExtensionContext context, String token) {
    context.getStore(ApiLoginExtension.NAMESPACE).put("token", token);
  }

  public static String getCodeVerifier(ExtensionContext context) {
    return context.getStore(ApiLoginExtension.NAMESPACE).get("code_verifier", String.class);
  }

  public static String getCodChallenge(ExtensionContext context) {
    return context.getStore(ApiLoginExtension.NAMESPACE).get("code_challenge", String.class);
  }

  public static String getCode(ExtensionContext context) {
    return context.getStore(ApiLoginExtension.NAMESPACE).get("code", String.class);
  }

  public static String getToken(ExtensionContext context) {
    return context.getStore(ApiLoginExtension.NAMESPACE).get("token", String.class);
  }

  public static String getCsrfToken() {
    return ThreadSafeCookieManager.INSTANCE.getCookieValue("XSRF-TOKEN");
  }

  public Cookie jsessionCookie() {
    return new Cookie(
        "JSESSIONID",
        ThreadSafeCookieManager.INSTANCE.getCookieValue("JSESSIONID")
    );
  }
}
