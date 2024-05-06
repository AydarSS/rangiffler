package project.qa.rangiffler.extension;

import com.github.javafaker.Faker;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;
import project.qa.rangiffler.annotation.TestUser;
import project.qa.rangiffler.annotation.UserParam;
import project.qa.rangiffler.annotation.WithPartners;
import project.qa.rangiffler.model.FriendStatus;
import project.qa.rangiffler.model.TestData;
import project.qa.rangiffler.model.User;

public abstract class AddUsersExtension implements BeforeEachCallback, ParameterResolver {

  private static final String PASSWORD = "12345";
  public static final ExtensionContext.Namespace ADD_USERS_NAMESPACE
      = ExtensionContext.Namespace.create(AddPhotosExtension.class);
  public static ThreadLocal<String> forXsrfToken = new ThreadLocal<>();

  @Override
  public void beforeEach(ExtensionContext extensionContext) {
    TestUser testUser =
        AnnotationSupport.findAnnotation(extensionContext.getRequiredTestMethod(), TestUser.class)
            .orElse(null);
    if (Objects.isNull(testUser)) {
      return;
    }
    User user;
    if (testUser.generateRandom()) {
      Faker faker = new Faker();
      user = User.withOnlyUsernameAndPassword(faker.name().username(), PASSWORD);
    } else {
      user = new User(null,
          testUser.username(),
          testUser.firstname(),
          testUser.lastname(),
          testUser.avatar(),
          null,
          null,
          null,
          null,
          null,
          new TestData(PASSWORD));
    }
    User saved = createUser(user);
    extensionContext.getStore(ADD_USERS_NAMESPACE).put(extensionContext.getUniqueId(), saved);
    List<WithPartners> partners = Arrays.stream(testUser.partners()).toList();
    for (WithPartners partner : partners) {
      savePartner(partner.status(), saved.username(), saved.id().toString());
    }
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext,
      ExtensionContext extensionContext) throws ParameterResolutionException {
    return
        AnnotationSupport.findAnnotation(parameterContext.getParameter(), UserParam.class)
            .isPresent()
            &&
            (parameterContext.getParameter().getType().isAssignableFrom(User.class));
  }

  @Override
  public Object resolveParameter(ParameterContext parameterContext,
      ExtensionContext extensionContext) throws ParameterResolutionException {
    User user = extensionContext.getStore(
        ADD_USERS_NAMESPACE).get(extensionContext.getUniqueId(), User.class);
    return user;
  }

  protected abstract User createUser(User user);

  protected abstract void savePartner(FriendStatus friendStatus, String currentUsername,
      String currentUserId);

}
