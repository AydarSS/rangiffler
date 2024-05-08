package project.qa.rangiffler.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface WithPhoto {

  String filename() default "";

  String username() default "";

  String countryCode() default "";

  String description() default "";

  boolean enterInMethod() default false;

  WithLike[] likes() default {};
}
