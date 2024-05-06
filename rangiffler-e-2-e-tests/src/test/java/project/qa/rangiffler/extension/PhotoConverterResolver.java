package project.qa.rangiffler.extension;

import com.github.jknack.handlebars.internal.Files;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;
import project.qa.rangiffler.annotation.PhotoFileConverter;
import project.qa.rangiffler.converter.PhotoConverter;

public class PhotoConverterResolver implements ParameterResolver {

  private final ClassLoader cl = PhotoConverter.class.getClassLoader();

  @Override
  public boolean supportsParameter(ParameterContext parameterContext,
      ExtensionContext extensionContext) throws ParameterResolutionException {
    return AnnotationSupport.isAnnotated(parameterContext.getParameter(), PhotoFileConverter.class)
        && parameterContext.getParameter().getType().isAssignableFrom(String.class);
  }

  @Override
  public Object resolveParameter(ParameterContext parameterContext,
      ExtensionContext extensionContext) throws ParameterResolutionException {
    PhotoFileConverter annotation = AnnotationSupport.findAnnotation(parameterContext.getParameter(), PhotoFileConverter.class).get();
    try (InputStream is = cl.getResourceAsStream(annotation.value())) {
      return Base64.getEncoder().encodeToString(is.readAllBytes());
    } catch (IOException e) {
      throw new ParameterResolutionException(e.getMessage());
    }
  }
}
