package project.qa.rangiffler.data.utils;

import com.google.protobuf.ByteString;
import guru.qa.grpc.rangiffler.UserOuterClass.User;
import java.util.Objects;
import project.qa.rangiffler.data.UserEntity;

public class UserEntityGrpcConverter {

  private static final String EMPTY_STRING = "";

  public static User fromUserEntity(UserEntity userEntity) {
    return User.newBuilder()
        .setId(userEntity.getId().toString())
        .setUsername(userEntity.getUsername())
        .setFirstname(setEmptyValueIfNull(userEntity.getFirstname()))
        .setSurname(setEmptyValueIfNull(userEntity.getSurname()))
        .setAvatar(ByteString.copyFrom(setEmptyValueIfNull(userEntity.getAvatar())))
        .build();
  }

  private static String setEmptyValueIfNull(String s) {
    if(Objects.isNull(s)) {
      return EMPTY_STRING;
    }
    return s;
  }

  private static byte[] setEmptyValueIfNull(byte[] s) {
    if(Objects.isNull(s)) {
      return new byte[0];
    }
    return s;
  }

}
