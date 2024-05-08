package project.qa.rangiffler.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import project.qa.rangiffler.model.FriendStatus;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface WithPartners {

  String firstname() default "";
  String lastname() default "";
  String avatar() default "";
  String countryCode() default "";

  FriendStatus status() default FriendStatus.NOT_FRIEND;

  AddedPhotos[] photos() default {};

}
