package project.qa.rangiffler.extension;

import com.google.protobuf.Timestamp;
import guru.qa.grpc.rangiffler.PhotoOuterClass.AddPhotoRequest;
import guru.qa.grpc.rangiffler.PhotoOuterClass.ChangeLikeRequest;
import guru.qa.grpc.rangiffler.PhotoOuterClass.PhotoResponse;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.UUID;
import project.qa.rangiffler.model.Country;
import project.qa.rangiffler.model.Likes;
import project.qa.rangiffler.model.Photo;
import project.qa.rangiffler.stub.PhotoServiceStub;

public class GrpcAddPhotoExtension extends AddPhotosExtension{

  @Override
  protected Photo addPhoto(String src, String username, String countryCode,String description ) {
    AddPhotoRequest request = AddPhotoRequest.newBuilder()
        .setUsername(username)
        .setCountryCode(countryCode)
        .setSrc(src)
        .setDescription(description)
        .build();

    PhotoResponse response = PhotoServiceStub.stub.addPhoto(request);
    return new Photo(
        UUID.fromString(response.getId()),
        response.getSrc(),
        Country.withOnlyCode(response.getCountryCode()),
        response.getDescription(),
        convertToLocalDate(response.getCreatedDate()),
        new Likes(0,null));
  }

  @Override
  protected void addLike(String username, String userId, String photoId) {
    ChangeLikeRequest request = ChangeLikeRequest.newBuilder()
        .setUsername(username)
        .setPhotoId(photoId)
        .setUserId(userId)
        .build();

    PhotoServiceStub.stub.changeLike(request);
  }

  private LocalDate convertToLocalDate(Timestamp timestamp) {
    Instant instant = Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    return instant.atZone(ZoneOffset.UTC).toLocalDate();
  }
}
