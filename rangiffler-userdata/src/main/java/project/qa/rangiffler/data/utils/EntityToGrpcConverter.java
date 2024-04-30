package project.qa.rangiffler.data.utils;

import guru.qa.grpc.rangiffler.UserOuterClass.Country;
import guru.qa.grpc.rangiffler.UserOuterClass.User;
import java.util.Objects;
import project.qa.rangiffler.data.CountryEntity;
import project.qa.rangiffler.data.UserEntity;

public class EntityToGrpcConverter {

  private static final String EMPTY_STRING = "";

  public static User fromUserEntity(UserEntity userEntity) {
    User.Builder user =  User.newBuilder()
        .setId(userEntity.getId().toString())
        .setUsername(userEntity.getUsername())
        .setFirstname(setEmptyValueIfNull(userEntity.getFirstname()))
        .setSurname(setEmptyValueIfNull(userEntity.getSurname()))
        .setAvatar(setEmptyValueIfNull(userEntity.getAvatar()));

    if (Objects.nonNull(userEntity.getCountry())) {
      user.setCountry(fromCountryEntity(userEntity.getCountry()));
    }

    return user.build();
  }

  public static Country fromCountryEntity(CountryEntity country){
    return Country.newBuilder()
        .setCode(country.getCode())
        .setName(country.getName())
        .setFlag(country.getFlag())
        .build();
  }

  public static String setEmptyValueIfNull(String s) {
    if (Objects.isNull(s)) {
      return EMPTY_STRING;
    }
    return s;
  }

}
