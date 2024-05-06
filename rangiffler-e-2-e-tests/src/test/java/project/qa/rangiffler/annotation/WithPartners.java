package project.qa.rangiffler.annotation;

import project.qa.rangiffler.model.FriendStatus;

public @interface WithPartners {

  FriendStatus status() default FriendStatus.NOT_FRIEND;


}
