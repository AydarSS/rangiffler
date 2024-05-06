package project.qa.rangiffler.extension;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;
import project.qa.rangiffler.annotation.PhotoId;
import project.qa.rangiffler.annotation.WithLike;
import project.qa.rangiffler.annotation.WithManyPhoto;
import project.qa.rangiffler.annotation.WithPhoto;
import project.qa.rangiffler.converter.PhotoConverter;
import project.qa.rangiffler.model.Photo;

public abstract class AddPhotosExtension implements BeforeEachCallback, ParameterResolver {

  public static final ExtensionContext.Namespace ADD_PHOTOS_NAMESPACE
      = ExtensionContext.Namespace.create(AddPhotosExtension.class);

  private final ClassLoader cl = PhotoConverter.class.getClassLoader();

  @Override
  public void beforeEach(ExtensionContext extensionContext) throws Exception {
    WithManyPhoto withManyPhoto = AnnotationSupport
        .findAnnotation(extensionContext.getRequiredTestMethod(), WithManyPhoto.class).orElse(null);
    if(Objects.isNull(withManyPhoto)) {
      return;
    }
    List<WithPhoto> withPhotos = Arrays.stream(withManyPhoto.value()).toList();
    checkThatEnterParameterInMethodIsOne(withPhotos);
    withPhotos.forEach(photo -> {

      Photo saved = savePhoto(photo);
      saveLike(photo, saved.id());

      if(photo.enterInMethod()) {
        extensionContext.getStore(ADD_PHOTOS_NAMESPACE)
            .put(extensionContext.getUniqueId(), saved.id());
      }
    });
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext,
      ExtensionContext extensionContext) throws ParameterResolutionException {
    return
        AnnotationSupport.findAnnotation(parameterContext.getParameter(), PhotoId.class).isPresent()
            &&
            (parameterContext.getParameter().getType().isAssignableFrom(String.class));
  }

  @Override
  public Object resolveParameter(ParameterContext parameterContext,
      ExtensionContext extensionContext) throws ParameterResolutionException {
    UUID photoId = extensionContext.getStore(
        ADD_PHOTOS_NAMESPACE).get(extensionContext.getUniqueId(), UUID.class);
    return photoId.toString();
  }

  protected abstract Photo addPhoto(String src, String username, String countryCode,String description);

  protected abstract void addLike(String username, String userId,String photoId);

  private String getSrcFromFile(String filename) {
    try (InputStream is = cl.getResourceAsStream(filename)) {
      return Base64.getEncoder().encodeToString(is.readAllBytes());
    } catch (IOException e) {
      throw new ParameterResolutionException(e.getMessage());
    }
  }

  private void checkThatEnterParameterInMethodIsOne(List<WithPhoto> withPhotos) {
    int countPhotosWithEnterInMethod = withPhotos.stream()
        .filter(WithPhoto::enterInMethod)
        .toList()
        .size();
    if(countPhotosWithEnterInMethod > 1 ) {
      throw new RuntimeException("More than one parameter enter to method");
    }
  }

  private Photo savePhoto(WithPhoto photo) {
    String src = getSrcFromFile(photo.filename());
    return addPhoto(src, photo.username(), photo.countryCode(), photo.description());
  }

  private void saveLike(WithPhoto photo, UUID photoId) {
    List<WithLike> likes = Arrays.stream(photo.likes()).toList();
    if(!likes.isEmpty()) {
      for (WithLike like: likes
      ) {
        addLike(like.username(), like.userId(), photoId.toString());
      }
    }
  }
}
