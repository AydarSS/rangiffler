package project.qa.rangiffler.service.grpc.utils;

import com.google.protobuf.ByteString;
import com.google.protobuf.Timestamp;
import guru.qa.grpc.rangiffler.PhotoOuterClass.Like;
import guru.qa.grpc.rangiffler.PhotoOuterClass.Photo;
import guru.qa.grpc.rangiffler.PhotoOuterClass.PhotoResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import project.qa.rangiffler.data.LikeEntity;
import project.qa.rangiffler.data.PhotoEntity;

public class GrpcToEntityConverter {

  public static List<Photo> toGrpcListPhotos(List<PhotoEntity> photos) {
    return photos.stream()
        .map(photo -> toGrpc(photo))
        .toList();
  }

  public static List<Like> toGrpcListLikes(List<LikeEntity> likes) {
    return likes.stream()
        .map(like -> toGrpc(like))
        .toList();
  }

  public static PhotoResponse toPhotoResponse(PhotoEntity photo) {
    return PhotoResponse.newBuilder()
        .setId(photo.getId().toString())
        .setUsername(photo.getUsername())
        .setSrc(photo.getPhoto())
        .setCountryCode(photo.getCountryCode())
        .setCreatedDate(convertLocalDateTimeToTimestamp(photo.getCreatedDate()))
        .setDescription(photo.getDescription())
        .build();
  }

  public static Timestamp convertLocalDateTimeToTimestamp(LocalDateTime localDateTime) {
    Instant instant = localDateTime.toInstant(ZoneOffset.UTC);

    Timestamp result = Timestamp.newBuilder()
        .setSeconds(instant.getEpochSecond())
        .setNanos(instant.getNano())
        .build();

    return result;
  }

  private static Photo toGrpc(PhotoEntity photo) {
    return Photo.newBuilder()
        .setId(photo.getId().toString())
        .setSrc(photo.getPhoto())
        .setUsername(photo.getUsername())
        .setCountryCode(photo.getCountryCode())
        .setDescription(photo.getDescription())
        .setCreatedDate(convertLocalDateTimeToTimestamp(photo.getCreatedDate()))
        .build();
  }

  private static Like toGrpc(LikeEntity like) {
    return Like.newBuilder()
        .setUserId(like.getUserId().toString())
        .setUsername(like.getUsername())
        .setCreatedDate(convertLocalDateTimeToTimestamp(like.getCreatedDate()))
        .build();
  }

}
