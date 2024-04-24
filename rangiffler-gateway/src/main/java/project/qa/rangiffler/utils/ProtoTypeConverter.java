package project.qa.rangiffler.utils;

import guru.qa.grpc.rangiffler.UserOuterClass;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import project.qa.rangiffler.model.query.Country;
import project.qa.rangiffler.model.query.User;

public class ProtoTypeConverter {

  public ProtoTypeConverter() {
  }

  public User fromProtoToUser(UserOuterClass.User protoUser) {
    return new User(
        UUID.fromString(protoUser.getId()),
        protoUser.getUsername(),
        protoUser.getSurname(),
        protoUser.getAvatar().toString(),
        new ArrayList<>(),
        new ArrayList<>(),
        new ArrayList<>(),
        fromProtoCountry(protoUser.getCountry())
    );
  }

  public List<User> fromProtoToListUsers(List<UserOuterClass.User> protoUserList) {
    return protoUserList
        .stream()
        .map(user -> fromProtoToUser(user))
        .toList();
  }

  private Country fromProtoCountry(UserOuterClass.Country protoCountry) {
    return new Country(protoCountry.getFlag(),
        protoCountry.getCode(),
        protoCountry.getName());
  }
}
