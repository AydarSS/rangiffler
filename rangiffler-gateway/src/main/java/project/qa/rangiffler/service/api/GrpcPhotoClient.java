package project.qa.rangiffler.service.api;

import guru.qa.grpc.rangiffler.PhotoOuterClass;
import guru.qa.grpc.rangiffler.PhotoOuterClass.AddPhotoRequest;
import guru.qa.grpc.rangiffler.PhotoOuterClass.ChangeLikeRequest;
import guru.qa.grpc.rangiffler.PhotoOuterClass.ChangePhotoRequest;
import guru.qa.grpc.rangiffler.PhotoOuterClass.DeletePhotoRequest;
import guru.qa.grpc.rangiffler.PhotoOuterClass.DeletePhotoResponse;
import guru.qa.grpc.rangiffler.PhotoOuterClass.GetLikesRequest;
import guru.qa.grpc.rangiffler.PhotoOuterClass.GetLikesResponse;
import guru.qa.grpc.rangiffler.PhotoOuterClass.GetPhotoByIdRequest;
import guru.qa.grpc.rangiffler.PhotoOuterClass.GetPhotosRequest;
import guru.qa.grpc.rangiffler.PhotoOuterClass.GetPhotosResponse;
import guru.qa.grpc.rangiffler.PhotoOuterClass.GetStatRequest;
import guru.qa.grpc.rangiffler.PhotoOuterClass.GetStatResponse;
import guru.qa.grpc.rangiffler.PhotoOuterClass.Paging;
import guru.qa.grpc.rangiffler.PhotoOuterClass.PhotoResponse;
import guru.qa.grpc.rangiffler.PhotoOuterClass.Username;
import guru.qa.grpc.rangiffler.PhotoServiceGrpc.PhotoServiceBlockingStub;
import java.util.List;
import java.util.UUID;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import project.qa.rangiffler.model.query.Like;
import project.qa.rangiffler.model.query.PageableObjects;
import project.qa.rangiffler.model.query.PageablePhoto;
import project.qa.rangiffler.model.query.Photo;
import project.qa.rangiffler.model.query.Stat;
import project.qa.rangiffler.service.api.utils.TypeConverter;

@Component
public class GrpcPhotoClient implements PhotoClient {

  private PhotoServiceBlockingStub photoServiceStub;
  private final TypeConverter typeConverter = new TypeConverter();

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

    return typeConverter.fromFrpc(response);
  }

  @Override
  public Photo changeLike(String username, UUID userID, UUID photoId) {
    ChangeLikeRequest request = ChangeLikeRequest.newBuilder()
        .setUsername(username)
        .setPhotoId(photoId.toString())
        .setUserId(userID.toString())
        .build();
    PhotoResponse response = photoServiceStub.changeLike(request);

    return typeConverter.fromFrpc(response);
  }

  @Override
  public Photo changePhoto(UUID photoId, String src, String countryCode, String description,
      String username) {
    ChangePhotoRequest request = ChangePhotoRequest.newBuilder()
        .setId(photoId.toString())
        .setCountryCode(countryCode)
        .setDescription(description)
        .build();
    PhotoResponse response = photoServiceStub.changePhoto(request);

    return typeConverter.fromFrpc(response);
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
    List<Username> usernamesProto = usernameList.stream()
        .map(us->Username.newBuilder().setUsername(us).build())
        .toList();

    GetPhotosRequest request = GetPhotosRequest.newBuilder()
        .addAllUsername(usernamesProto)
        .setPageInfo(Paging.newBuilder()
            .setSize(size)
            .setPage(page)
            .build())
        .build();

    GetPhotosResponse response = photoServiceStub.getPhotos(request);
    List<PhotoOuterClass.Photo> listPhoto = response.getPhotosList();
    List<Photo> photos = listPhoto
        .stream()
        .map(typeConverter::fromGrpc)
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
        .map(typeConverter::fromGrpc)
        .toList();
  }

  @Override
  public List<Stat> getStat(List<String> users) {
    List<Username> usernamesProto = users.stream()
        .map(us->Username.newBuilder().setUsername(us).build())
        .toList();
    GetStatRequest request = GetStatRequest.newBuilder()
        .addAllUsername(usernamesProto)
        .build();

    GetStatResponse response = photoServiceStub.getStat(request);
    return response.getStatisticList()
        .stream()
        .map(typeConverter::fromGrpc)
        .toList();
  }

  @Override
  public String getCreatedPhotoUsernameByPhotoId(UUID photoId) {
    GetPhotoByIdRequest request = GetPhotoByIdRequest.newBuilder()
        .setId(photoId.toString())
        .build();

    PhotoResponse response = photoServiceStub.getPhoto(request);

    return response.getUsername();
  }
}
