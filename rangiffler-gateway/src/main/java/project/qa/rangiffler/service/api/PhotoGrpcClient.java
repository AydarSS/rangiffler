package project.qa.rangiffler.service.api;

import com.google.protobuf.Timestamp;
import guru.qa.grpc.rangiffler.PhotoOuterClass;
import guru.qa.grpc.rangiffler.PhotoOuterClass.AddPhotoRequest;
import guru.qa.grpc.rangiffler.PhotoOuterClass.ChangeLikeRequest;
import guru.qa.grpc.rangiffler.PhotoOuterClass.ChangePhotoRequest;
import guru.qa.grpc.rangiffler.PhotoOuterClass.DeletePhotoRequest;
import guru.qa.grpc.rangiffler.PhotoOuterClass.DeletePhotoResponse;
import guru.qa.grpc.rangiffler.PhotoOuterClass.GetLikesRequest;
import guru.qa.grpc.rangiffler.PhotoOuterClass.GetLikesResponse;
import guru.qa.grpc.rangiffler.PhotoOuterClass.GetPhotosRequest;
import guru.qa.grpc.rangiffler.PhotoOuterClass.GetPhotosResponse;
import guru.qa.grpc.rangiffler.PhotoOuterClass.GetStatRequest;
import guru.qa.grpc.rangiffler.PhotoOuterClass.GetStatResponse;
import guru.qa.grpc.rangiffler.PhotoOuterClass.PageableRequestWithoutSearchQuery;
import guru.qa.grpc.rangiffler.PhotoOuterClass.PhotoResponse;
import guru.qa.grpc.rangiffler.PhotoOuterClass.Statistic;
import guru.qa.grpc.rangiffler.PhotoServiceGrpc.PhotoServiceBlockingStub;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import project.qa.rangiffler.model.query.Country;
import project.qa.rangiffler.model.query.Like;
import project.qa.rangiffler.model.query.PageableObjects;
import project.qa.rangiffler.model.query.PageablePhoto;
import project.qa.rangiffler.model.query.Photo;
import project.qa.rangiffler.model.query.Stat;

@Component
public class PhotoGrpcClient implements PhotoClient {

  private PhotoServiceBlockingStub photoServiceStub;

  @GrpcClient("grpcPhotoClient")
  public void setPhotoServiceStub(PhotoServiceBlockingStub photoServiceStub) {
    this.photoServiceStub = photoServiceStub;
  }

  @Override
  public Photo addPhoto(String src, String countryCode, String description, String username) {
    AddPhotoRequest request = AddPhotoRequest.newBuilder()
        .setUsername(username)
        .setCountryCode(countryCode)
        .setSrc(src)
        .setDescription(description)
        .build();

    PhotoResponse response = photoServiceStub.addPhoto(request);

    return new Photo(
        UUID.fromString(response.getId()),
        response.getSrc(),
        Country.withOnlyCode(response.getCountryCode()),
        response.getDescription(),
        convertToLocalDate(response.getCreatedDate()),
        null);
  }

  @Override
  public Photo changeLike(String username, UUID userID, UUID photoId) {
    ChangeLikeRequest request = ChangeLikeRequest.newBuilder()
        .setUsername(username)
        .setPhotoId(photoId.toString())
        .setUserId(userID.toString())
        .build();
    PhotoResponse response = photoServiceStub.changeLike(request);

    return new Photo(
        UUID.fromString(response.getId()),
        response.getSrc(),
        Country.withOnlyCode(response.getCountryCode()),
        response.getDescription(),
        convertToLocalDate(response.getCreatedDate()),
        null);
  }

  @Override
  public Photo changePhoto(UUID photoId, String src, String countryCode, String description,
      String username) {
    ChangePhotoRequest request = ChangePhotoRequest.newBuilder()
        .setId(photoId.toString())
        .setSrc(src)
        .setCountryCode(countryCode)
        .setDescription(description)
        .build();
    PhotoResponse response = photoServiceStub.changePhoto(request);

    return new Photo(
        UUID.fromString(response.getId()),
        response.getSrc(),
        Country.withOnlyCode(response.getCountryCode()),
        response.getDescription(),
        convertToLocalDate(response.getCreatedDate()),
        null);
  }

  @Override
  public boolean deletePhoto(UUID photoId) {
    DeletePhotoRequest request = DeletePhotoRequest.newBuilder()
        .setId(photoId.toString())
        .build();
    DeletePhotoResponse response = photoServiceStub.deletePhoto(request);
    return response.getIsSuccess();
  }

  @Override
  public PageableObjects<Photo> getPhotos(List<String> usernameList, int size, int page) {
    GetPhotosRequest request = GetPhotosRequest.newBuilder()
        .addAllUsername(usernameList)
        .setPageInfo(PageableRequestWithoutSearchQuery.newBuilder()
            .setSize(size)
            .setPage(page)
            .build())
        .build();
    GetPhotosResponse response = photoServiceStub.getPhotos(request);
    List<PhotoOuterClass.Photo> listPhoto = response.getPhotosList();
    List<Photo> photos = listPhoto
        .stream()
        .map(this::fromGrpc)
        .toList();
    return new PageablePhoto(photos, response.getHasNext());
  }

  @Override
  public List<Like> getLikes(UUID photoId) {
    GetLikesRequest request = GetLikesRequest.newBuilder()
        .setPhotoId(photoId.toString())
        .build();
    GetLikesResponse response = photoServiceStub.getLikes(request);

    return response.getLikesList()
        .stream()
        .map(this::fromGrpc)
        .toList();
  }

  @Override
  public List<Stat> getStat(List<String> users) {
    GetStatRequest request = GetStatRequest.newBuilder()
        .addAllUsername(users)
        .build();

    GetStatResponse response = photoServiceStub.getStat(request);
    return response.getStatisticList()
        .stream()
        .map(this::fromGrpc)
        .toList();
  }

  private LocalDate convertToLocalDate(Timestamp timestamp) {
    Instant instant = Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    LocalDate time = instant.atZone(ZoneOffset.UTC).toLocalDate();
    return time;
  }

  private Photo fromGrpc(PhotoOuterClass.Photo grpcPhoto) {
    return new Photo(
        UUID.fromString(grpcPhoto.getId()),
        grpcPhoto.getSrc(),
        Country.withOnlyCode(grpcPhoto.getCountryCode()),
        grpcPhoto.getDescription(),
        convertToLocalDate(grpcPhoto.getCreatedDate()),
        null);
  }

  private Like fromGrpc(PhotoOuterClass.Like grpcLike) {
    return new Like(UUID.fromString(grpcLike.getUserId()),
        grpcLike.getUsername(),
        convertToLocalDate(grpcLike.getCreatedDate()));
  }

  private Stat fromGrpc(Statistic statistic) {
    return new Stat(statistic.getCount(), Country.withOnlyCode(statistic.getCountryCode()));
  }
}
