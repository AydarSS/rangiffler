package project.qa.rangiffler.extension.grpc.client;

import guru.qa.grpc.rangiffler.PhotoOuterClass.AddPhotoRequest;
import guru.qa.grpc.rangiffler.PhotoOuterClass.ChangeLikeRequest;
import guru.qa.grpc.rangiffler.PhotoOuterClass.ChangePhotoRequest;
import guru.qa.grpc.rangiffler.PhotoOuterClass.DeletePhotoRequest;
import guru.qa.grpc.rangiffler.PhotoOuterClass.DeletePhotoResponse;
import guru.qa.grpc.rangiffler.PhotoOuterClass.GetLikesRequest;
import guru.qa.grpc.rangiffler.PhotoOuterClass.GetLikesResponse;
import guru.qa.grpc.rangiffler.PhotoOuterClass.GetPhotoByIdRequest;
import guru.qa.grpc.rangiffler.PhotoOuterClass.GetStatRequest;
import guru.qa.grpc.rangiffler.PhotoOuterClass.GetStatResponse;
import guru.qa.grpc.rangiffler.PhotoOuterClass.PhotoResponse;
import guru.qa.grpc.rangiffler.PhotoOuterClass.Username;
import java.util.List;
import java.util.UUID;
import project.qa.rangiffler.extension.grpc.utils.TypeConverter;
import project.qa.rangiffler.model.Like;
import project.qa.rangiffler.model.Photo;
import project.qa.rangiffler.model.Stat;
import project.qa.rangiffler.stub.PhotoServiceStub;

public class GrpcPhotoClient {

  private final TypeConverter typeConverter = new TypeConverter();

  public Photo addPhoto(String src, String countryCode, String description, String username) {
    AddPhotoRequest request;
    PhotoResponse response;

    request = AddPhotoRequest.newBuilder()
        .setUsername(username)
        .setCountryCode(countryCode)
        .setSrc(src)
        .setDescription(description)
        .build();

    response = PhotoServiceStub.stub.addPhoto(request);
    return typeConverter.fromFrpc(response);
  }

  public Photo changeLike(String username, UUID userID, UUID photoId) {
    ChangeLikeRequest request;
    PhotoResponse response;
    request = ChangeLikeRequest.newBuilder()
        .setUsername(username)
        .setPhotoId(photoId.toString())
        .setUserId(userID.toString())
        .build();
    response = PhotoServiceStub.stub.changeLike(request);

    return typeConverter.fromFrpc(response);
  }

  public Photo changePhoto(UUID photoId, String src, String countryCode, String description,
      String username) {
    ChangePhotoRequest request;
    PhotoResponse response;
    request = ChangePhotoRequest.newBuilder()
        .setId(photoId.toString())
        .setCountryCode(countryCode)
        .setDescription(description)
        .build();
    response = PhotoServiceStub.stub.changePhoto(request);

    return typeConverter.fromFrpc(response);
  }

  public boolean deletePhoto(UUID photoId) {
    DeletePhotoRequest request;
    DeletePhotoResponse response;

    request = DeletePhotoRequest.newBuilder()
        .setId(photoId.toString())
        .build();
    response = PhotoServiceStub.stub.deletePhoto(request);

    return response.getIsSuccess();
  }

  public List<Like> getLikes(UUID photoId) {
    GetLikesRequest request;
    GetLikesResponse response;

    request = GetLikesRequest.newBuilder()
        .setPhotoId(photoId.toString())
        .build();
    response = PhotoServiceStub.stub.getLikes(request);

    return response.getLikesList()
        .stream()
        .map(typeConverter::fromGrpc)
        .toList();
  }

  public List<Stat> getStat(List<String> users) {
    GetStatRequest request;
    GetStatResponse response;
    List<Username> usernamesProto = users.stream()
        .map(us -> Username.newBuilder().setUsername(us).build())
        .toList();
    request = GetStatRequest.newBuilder()
        .addAllUsername(usernamesProto)
        .build();

    response = PhotoServiceStub.stub.getStat(request);

    return response.getStatisticList()
        .stream()
        .map(typeConverter::fromGrpc)
        .toList();
  }

  public String getCreatedPhotoUsernameByPhotoId(UUID photoId) {
    GetPhotoByIdRequest request;
    PhotoResponse response;

    request = GetPhotoByIdRequest.newBuilder()
        .setId(photoId.toString())
        .build();

    response = PhotoServiceStub.stub.getPhoto(request);

    return response.getUsername();
  }
}
