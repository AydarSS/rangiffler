package project.qa.rangiffler.extension;

import com.google.protobuf.Timestamp;
import guru.qa.grpc.rangiffler.PhotoOuterClass.AddPhotoRequest;
import guru.qa.grpc.rangiffler.PhotoOuterClass.ChangeLikeRequest;
import guru.qa.grpc.rangiffler.PhotoOuterClass.PhotoResponse;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.UUID;
import project.qa.rangiffler.extension.grpc.client.GrpcPhotoClient;
import project.qa.rangiffler.model.Country;
import project.qa.rangiffler.model.Likes;
import project.qa.rangiffler.model.Photo;
import project.qa.rangiffler.stub.PhotoServiceStub;

public class GrpcAddPhotoExtension extends AddPhotosExtension{

  private final GrpcPhotoClient client = new GrpcPhotoClient();

  @Override
  protected Photo addPhoto(String src, String username, String countryCode,String description ) {
    return client.addPhoto(src,countryCode,description,username);
  }

  @Override
  protected void addLike(String username, String userId, String photoId) {
    client.changeLike(username,UUID.fromString(userId), UUID.fromString(photoId));
  }

  private LocalDate convertToLocalDate(Timestamp timestamp) {
    Instant instant = Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    return instant.atZone(ZoneOffset.UTC).toLocalDate();
  }
}
