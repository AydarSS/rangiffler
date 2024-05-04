package project.qa.rangiffler.service.grpc.utils;

import com.google.protobuf.ByteString;
import guru.qa.grpc.rangiffler.UserOuterClass.FriendsStatus;
import guru.qa.grpc.rangiffler.UserOuterClass.User;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;
import project.qa.rangiffler.data.UserEntity;

public class EntityToGrpcConverter {

  private static final String EMPTY_STRING = "";

  public static User to(UserEntity userEntity) {
    User.Builder user = baseUser(userEntity);
    return user.build();
  }

  public static User withFriendStatus(UserEntity userEntity,
      FriendsStatus friendsStatus) {
    User.Builder user = baseUser(userEntity);
    user.setFriendStatus(friendsStatus);
    return user.build();
  }

  public static User withFriendStatusResolver(
      UserEntity userEntity, Supplier<FriendsStatus> friendsStatusResolver) {
    User.Builder user = baseUser(userEntity);
    user.setFriendStatus(friendsStatusResolver.get());
    return user.build();
  }

  private static User.Builder baseUser(UserEntity userEntity) {
    return User.newBuilder()
        .setId(userEntity.getId().toString())
        .setUsername(userEntity.getUsername())
        .setFirstname(setEmptyValueIfNull(userEntity.getFirstname()))
        .setSurname(setEmptyValueIfNull(userEntity.getSurname()))
        .setAvatar(setEmptyValueIfNull(userEntity.getAvatar()))
        .setCountryId(setEmptyValueIfNull(userEntity.getCountryId()));
  }

  private static String setEmptyValueIfNull(String s) {
    if (Objects.isNull(s)) {
      return EMPTY_STRING;
    }
    return s;
  }

  private static String setEmptyValueIfNull(UUID s) {
    if (Objects.isNull(s)) {
      return EMPTY_STRING;
    }
    return s.toString();
  }

}
