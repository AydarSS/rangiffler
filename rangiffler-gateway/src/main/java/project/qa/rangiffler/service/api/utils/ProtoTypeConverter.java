package project.qa.rangiffler.service.api.utils;

import guru.qa.grpc.rangiffler.UserOuterClass;
import guru.qa.grpc.rangiffler.UserOuterClass.FriendsStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import project.qa.rangiffler.model.query.Country;
import project.qa.rangiffler.model.query.FriendStatus;
import project.qa.rangiffler.model.query.User;

public class ProtoTypeConverter {

  public ProtoTypeConverter() {
  }

  public User fromProtoToUser(UserOuterClass.User protoUser) {
    return new User(
        UUID.fromString(protoUser.getId()),
        protoUser.getUsername(),
        protoUser.getFirstname(),
        protoUser.getSurname(),
        protoUser.getAvatar(),
        protoUser.getFriendStatus().equals(FriendsStatus.NOT_FRIEND) ||
            protoUser.getFriendStatus().equals(FriendsStatus.UNSPECIFIED)
            ?
            null :
            FriendStatus.valueOf(protoUser.getFriendStatus().name()),
        new ArrayList<>(),
        new ArrayList<>(),
        new ArrayList<>(),
        Country.withOnlyId(protoUser.getCountryId())
    );
  }

  public List<User> fromProtoToListUsers(List<UserOuterClass.User> protoUserList) {
    return protoUserList
        .stream()
        .map(this::fromProtoToUser)
        .toList();
  }

}
