package project.qa.rangiffler.data.utils;

import guru.qa.grpc.rangiffler.UserOuterClass.User;
import java.util.Objects;
import project.qa.rangiffler.data.UserEntity;

public class EntityToGrpcConverter {

  private static final String EMPTY_STRING = "";

  public static User fromUserEntity(UserEntity userEntity) {
    User.Builder user = User.newBuilder()
        .setId(userEntity.getId().toString())
        .setUsername(userEntity.getUsername())
        .setFirstname(setEmptyValueIfNull(userEntity.getFirstname()))
        .setSurname(setEmptyValueIfNull(userEntity.getSurname()))
        .setAvatar(setEmptyValueIfNull(userEntity.getAvatar()));

    if (Objects.nonNull(userEntity.getCountryId())) {
      user.setCountryId(user.getCountryId());
    }

    return user.build();
  }

  public static String setEmptyValueIfNull(String s) {
    if (Objects.isNull(s)) {
      return EMPTY_STRING;
    }
    return s;
  }

}
