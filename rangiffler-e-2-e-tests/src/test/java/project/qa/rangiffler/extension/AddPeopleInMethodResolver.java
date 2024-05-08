package project.qa.rangiffler.extension;

import static project.qa.rangiffler.extension.AddUsersExtension.ADD_USERS_NAMESPACE;

import java.util.Map;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;
import project.qa.rangiffler.annotation.People;

public class AddPeopleInMethodResolver implements ParameterResolver {

  public static final ExtensionContext.Namespace ADD_PEOPLE_IN_METHOD_NAMESPACE
      = ExtensionContext.Namespace.create(AddPeopleInMethodResolver.class);

  @Override
  public boolean supportsParameter(ParameterContext parameterContext,
      ExtensionContext extensionContext) throws ParameterResolutionException {
    return AnnotationSupport.findAnnotation(parameterContext.getParameter(), People.class)
        .isPresent() &&
        (parameterContext.getParameter().getType().isAssignableFrom(Map.class));
  }

  @Override
  public Object resolveParameter(ParameterContext parameterContext,
      ExtensionContext extensionContext) throws ParameterResolutionException {
    Map peopleMap = extensionContext.getStore(
        ADD_PEOPLE_IN_METHOD_NAMESPACE).get(extensionContext.getUniqueId(), Map.class);
    return peopleMap;
  }
}
