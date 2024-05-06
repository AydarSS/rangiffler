package project.qa.rangiffler.converter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ArgumentConverter;
import project.qa.rangiffler.annotation.PhotoFileConverter;

public class PhotoConverter implements ArgumentConverter {

  private final ClassLoader cl = PhotoConverter.class.getClassLoader();

  @Override
  public Object convert(Object o, ParameterContext parameterContext)
      throws ArgumentConversionException {
    if(!(o instanceof String) || !parameterContext.isAnnotated(PhotoFileConverter.class)) {
      throw new RuntimeException("Cannot read file");
    }
    try (InputStream is = cl.getResourceAsStream((String) o)) {
      return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new ArgumentConversionException(e.getMessage());
    }
  }
}
