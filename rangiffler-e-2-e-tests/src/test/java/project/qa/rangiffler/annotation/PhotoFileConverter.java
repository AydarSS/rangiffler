package project.qa.rangiffler.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.converter.ConvertWith;
import project.qa.rangiffler.extension.PhotoConverterResolver;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@ExtendWith(PhotoConverterResolver.class)
public @interface PhotoFileConverter {

  String value() default "";
}
