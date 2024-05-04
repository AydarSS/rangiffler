package project.qa.rangiffler.service.api.utils;

import com.google.protobuf.Timestamp;
import guru.qa.grpc.rangiffler.CountryOuterClass;
import guru.qa.grpc.rangiffler.PhotoOuterClass;
import guru.qa.grpc.rangiffler.PhotoOuterClass.PhotoResponse;
import guru.qa.grpc.rangiffler.PhotoOuterClass.Statistic;
import guru.qa.grpc.rangiffler.UserOuterClass;
import guru.qa.grpc.rangiffler.UserOuterClass.FriendsStatus;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import project.qa.rangiffler.model.query.Country;
import project.qa.rangiffler.model.query.FriendStatus;
import project.qa.rangiffler.model.query.Like;
import project.qa.rangiffler.model.query.Likes;
import project.qa.rangiffler.model.query.Photo;
import project.qa.rangiffler.model.query.Stat;
import project.qa.rangiffler.model.query.User;

public class TypeConverter {

  private final String STRING_EMPTY = "";

  public TypeConverter() {
  }

  public  Country fromGrpc(CountryOuterClass.Country grpcCountry) {
    return new Country(UUID.fromString(grpcCountry.getId()),
        grpcCountry.getFlag(),
        grpcCountry.getCode(),
        grpcCountry.getName());
  }

  public User fromProtoToUser(UserOuterClass.User protoUser) {
    return new User(
        UUID.fromString(protoUser.getId()),
        protoUser.getUsername(),
        protoUser.getFirstname(),
        protoUser.getSurname(),
        protoUser.getAvatar().toString(),
        protoUser.getFriendStatus().equals(FriendsStatus.NOT_FRIEND) ||
            protoUser.getFriendStatus().equals(FriendsStatus.UNSPECIFIED)
            ?
            null :
            FriendStatus.valueOf(protoUser.getFriendStatus().name()),
        new ArrayList<>(),
        new ArrayList<>(),
        new ArrayList<>(),
        protoUser.getCountryId().equals(STRING_EMPTY) ?
            null :
        Country.withOnlyId(protoUser.getCountryId())
    );
  }

  public List<User> fromProtoToListUsers(List<UserOuterClass.User> protoUserList) {
    return protoUserList
        .stream()
        .map(this::fromProtoToUser)
        .toList();
  }

  public Photo fromFrpc(PhotoResponse response) {
    return new Photo(
        UUID.fromString(response.getId()),
        response.getSrc().toString(),
        Country.withOnlyCode(response.getCountryCode()),
        response.getDescription(),
        convertToLocalDate(response.getCreatedDate()),
        new Likes(0,null));
  }

  private LocalDate convertToLocalDate(Timestamp timestamp) {
    Instant instant = Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    LocalDate time = instant.atZone(ZoneOffset.UTC).toLocalDate();
    return time;
  }

  public Photo fromGrpc(PhotoOuterClass.Photo grpcPhoto) {
    return new Photo(
        UUID.fromString(grpcPhoto.getId()),
        grpcPhoto.getSrc(),
        Country.withOnlyCode(grpcPhoto.getCountryCode()),
        grpcPhoto.getDescription(),
        convertToLocalDate(grpcPhoto.getCreatedDate()),
        null);
  }

  public Like fromGrpc(PhotoOuterClass.Like grpcLike) {
    return new Like(UUID.fromString(grpcLike.getUserId()),
        grpcLike.getUsername(),
        convertToLocalDate(grpcLike.getCreatedDate()));
  }

  public Stat fromGrpc(Statistic statistic) {
    return new Stat(statistic.getCount(), Country.withOnlyCode(statistic.getCountryCode()));
  }
}
