package project.qa.rangiffler.extension;

import static project.qa.rangiffler.extension.AddPeopleInMethodResolver.ADD_PEOPLE_IN_METHOD_NAMESPACE;

import com.github.javafaker.Faker;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;
import project.qa.rangiffler.annotation.AddedPhotos;
import project.qa.rangiffler.annotation.ApiLogin;
import project.qa.rangiffler.annotation.LikeInfo;
import project.qa.rangiffler.annotation.TestUser;
import project.qa.rangiffler.annotation.UserParam;
import project.qa.rangiffler.annotation.WithLike;
import project.qa.rangiffler.annotation.WithPartners;
import project.qa.rangiffler.converter.PhotoConverter;
import project.qa.rangiffler.model.Country;
import project.qa.rangiffler.model.FriendStatus;
import project.qa.rangiffler.model.Photo;
import project.qa.rangiffler.model.TestData;
import project.qa.rangiffler.model.User;

public abstract class AddUsersExtension implements BeforeEachCallback, ParameterResolver {

  private static final String PASSWORD = "12345";
  public static final ExtensionContext.Namespace ADD_USERS_NAMESPACE
      = ExtensionContext.Namespace.create(AddUsersExtension.class);
  public static ThreadLocal<String> xsrfTokenHolder = new ThreadLocal<>();
  private final ClassLoader cl = PhotoConverter.class.getClassLoader();
  private final String LOGINNED_USER = "LOGINNED_USER";
  private final String PARTNER_USER = "PARTNER_USER";
  private final String STRING_EMPTY = "";

  @Override
  public void beforeEach(ExtensionContext extensionContext) {
    User user;
    Map<String, User> userMap = new HashMap<>();
    Map<User, FriendStatus> peopleMap = new HashMap<>();
    TestUser testUser;

    TestUser testUserOuter =
        AnnotationSupport.findAnnotation(extensionContext.getRequiredTestMethod(), TestUser.class)
            .orElse(null);
    TestUser userInApiLogin =
        Objects.requireNonNull(
            AnnotationSupport.findAnnotation(extensionContext.getRequiredTestMethod(),
                    ApiLogin.class)
                .map(ApiLogin::user)
                .orElse(null));

    if (Objects.isNull(testUserOuter) && Objects.isNull(userInApiLogin)) {
      return;
    }
    testUser = Objects.isNull(testUserOuter) ? userInApiLogin : testUserOuter;

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
    userMap.put(LOGINNED_USER, saved);
    resolvePhotos(userMap, saved, Arrays.asList(testUser.photos()));
    extensionContext.getStore(ADD_USERS_NAMESPACE).put(extensionContext.getUniqueId(), saved);
    List<WithPartners> partners = Arrays.stream(testUser.partners()).toList();
    for (WithPartners partner : partners) {
      User partnerUser = resolvePartner(partner, saved);
      peopleMap.put(partnerUser, partner.status());
      userMap.put(PARTNER_USER, partnerUser);
      resolvePhotos(userMap,
          partnerUser,
          Arrays.asList(partner.photos()));
    }
    extensionContext.getStore(ADD_PEOPLE_IN_METHOD_NAMESPACE).put(extensionContext.getUniqueId(), peopleMap);
  }

  private void resolvePhotos(Map<String, User> userMap,
      User userAddedPhoto,
      List<AddedPhotos> photos) {
    if (photos.size() > 0) {
      for (AddedPhotos photoToAdd : photos
      ) {
        Photo savedPhoto = savePhoto(photoToAdd, userAddedPhoto.username());
        addLikes(userMap,savedPhoto.id(),photoToAdd.likes());
      }
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

  protected abstract User savePartner(User partner,
      String currentUsername,
      String currentUserId);

  protected abstract Photo addPhoto(String src, String username, String countryCode,
      String description);

  protected abstract void addLike(String username, String userId, String photoId);

  private void addLikes(Map<String, User> userMap, UUID photoId, WithLike likes) {
      LikeInfo likeInfo = likes.likeInfo();
      if (likeInfo.withLCreatedPhotoUser() && userMap.containsKey(PARTNER_USER)) {
        addLike(userMap.get(PARTNER_USER).username(),
            userMap.get(PARTNER_USER).id().toString(),
            photoId.toString());
      }
      if (likeInfo.withLikeAuthorizedUser()  && userMap.containsKey(LOGINNED_USER)) {
        addLike(userMap.get(LOGINNED_USER).username(),
            userMap.get(LOGINNED_USER).id().toString(),
            photoId.toString());
      }
      for (int i = 0; i < likeInfo.countLikes(); i++) {
        Faker fakerRandom = new Faker();
        User randomUser = User.withOnlyUsernameAndPassword(fakerRandom.name().username(), PASSWORD);
        User savedRandomUser = createUser(randomUser);
        addLike(savedRandomUser.username(),
            savedRandomUser.id().toString(),
            photoId.toString());
      }
  }

  private Photo savePhoto(AddedPhotos photo, String username) {
    String src = getSrcFromFile(photo.filename());
    return addPhoto(src, username, photo.countryCode(), photo.description());
  }

  private String getSrcFromFile(String filename) {
    try (InputStream is = cl.getResourceAsStream(filename)) {
      return "data:image/png;base64," + Base64.getEncoder().encodeToString(is.readAllBytes());
    } catch (IOException e) {
      throw new ParameterResolutionException(e.getMessage());
    }
  }

  private User resolvePartner(WithPartners partner, User saved) {
    User generatedUser;
    Faker faker = new Faker();
    if(ifNotExistsFilledFieldsIn(partner)) {
      generatedUser = new User(
          null,
          faker.name().username(),
          STRING_EMPTY,
          STRING_EMPTY,
          STRING_EMPTY,
          partner.status(),
          null,
          null,
          null,
          new Country(null, STRING_EMPTY, STRING_EMPTY, STRING_EMPTY),
          new TestData(PASSWORD));
    } else {
      generatedUser = new User(
          null,
          faker.name().username(),
          partner.firstname(),
          partner.lastname(),
          partner.avatar(),
          partner.status(),
          null,
          null,
          null,
          new Country(null, null, partner.countryCode(), null),
          new TestData(PASSWORD));
    }
    return savePartner(generatedUser,
        saved.username(),
        saved.id().toString());
  }

  private boolean ifNotExistsFilledFieldsIn(WithPartners partner){
    return partner.firstname().equals(STRING_EMPTY) &&
        partner.lastname().equals(STRING_EMPTY) &&
        partner.avatar().equals(STRING_EMPTY) &&
        partner.countryCode().equals(STRING_EMPTY);
  }

}
