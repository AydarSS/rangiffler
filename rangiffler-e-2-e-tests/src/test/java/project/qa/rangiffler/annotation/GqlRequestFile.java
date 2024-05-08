package project.qa.rangiffler.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;
import project.qa.rangiffler.extension.GqlRequestResolver;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@ExtendWith(GqlRequestResolver.class)
public @interface GqlRequestFile {

  String value();
}
