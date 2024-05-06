package project.qa.rangiffler.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TestUser {

  String username() default "";

  String firstname() default "";

  String lastname() default "";

  String avatar() default "";

  String countryId() default "";

  WithPartners[] partners() default {};

  boolean generateRandom() default false;


}
