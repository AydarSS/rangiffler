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
import io.grpc.StatusRuntimeException;
import java.util.List;
import java.util.UUID;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import project.qa.rangiffler.model.query.Like;
import project.qa.rangiffler.model.query.PageableObjects;
import project.qa.rangiffler.model.query.PageablePhoto;
import project.qa.rangiffler.model.query.Photo;
import project.qa.rangiffler.model.query.Stat;
import project.qa.rangiffler.service.api.utils.TypeConverter;

@Component
public class GrpcPhotoClient implements PhotoClient {

  private static final Logger LOG = LoggerFactory.getLogger(GrpcPhotoClient.class);
  private PhotoServiceBlockingStub photoServiceStub;
  private final TypeConverter typeConverter = new TypeConverter();
  private final static String CALLING_GRPC_ERROR_TEXT = "### Error while calling gRPC server to Photo service";

  @GrpcClient("grpcPhotoClient")
  public void setPhotoServiceStub(PhotoServiceBlockingStub photoServiceStub) {
    this.photoServiceStub = photoServiceStub;
  }

  @Override
  public Photo addPhoto(String src, String countryCode, String description, String username) {
    AddPhotoRequest request;
    PhotoResponse response;
    try {
      request = AddPhotoRequest.newBuilder()
          .setUsername(username)
          .setCountryCode(countryCode)
          .setSrc(src)
          .setDescription(description)
          .build();

      response = photoServiceStub.addPhoto(request);
    } catch (StatusRuntimeException e) {
      LOG.error(CALLING_GRPC_ERROR_TEXT, e);
      throw new RuntimeException(e);
    }

    return typeConverter.fromFrpc(response);
  }

  @Override
  public Photo changeLike(String username, UUID userID, UUID photoId) {
    ChangeLikeRequest request;
    PhotoResponse response;
    try {
      request = ChangeLikeRequest.newBuilder()
          .setUsername(username)
          .setPhotoId(photoId.toString())
          .setUserId(userID.toString())
          .build();
      response = photoServiceStub.changeLike(request);
    } catch (StatusRuntimeException e) {
      LOG.error(CALLING_GRPC_ERROR_TEXT, e);
      throw new RuntimeException(e);
    }
    return typeConverter.fromFrpc(response);
  }

  @Override
  public Photo changePhoto(UUID photoId, String src, String countryCode, String description,
      String username) {
    ChangePhotoRequest request;
    PhotoResponse response;
    try {
      request = ChangePhotoRequest.newBuilder()
          .setId(photoId.toString())
          .setCountryCode(countryCode)
          .setDescription(description)
          .build();
      response = photoServiceStub.changePhoto(request);
    } catch (StatusRuntimeException e) {
      LOG.error(CALLING_GRPC_ERROR_TEXT, e);
      throw new RuntimeException(e);
    }
    return typeConverter.fromFrpc(response);
  }

  @Override
  public boolean deletePhoto(UUID photoId) {
    DeletePhotoRequest request;
    DeletePhotoResponse response;
    try {
      request = DeletePhotoRequest.newBuilder()
          .setId(photoId.toString())
          .build();
      response = photoServiceStub.deletePhoto(request);
    } catch (StatusRuntimeException e) {
      LOG.error(CALLING_GRPC_ERROR_TEXT, e);
      throw new RuntimeException(e);
    }
    return response.getIsSuccess();
  }

  @Override
  public PageableObjects<Photo> getPhotos(List<String> usernameList, int size, int page) {
    GetPhotosRequest request;
    GetPhotosResponse response;

    List<Username> usernamesProto = usernameList.stream()
        .map(us -> Username.newBuilder().setUsername(us).build())
        .toList();

    try {
      request = GetPhotosRequest.newBuilder()
          .addAllUsername(usernamesProto)
          .setPageInfo(Paging.newBuilder()
              .setSize(size)
              .setPage(page)
              .build())
          .build();

      response = photoServiceStub.getPhotos(request);
    } catch (StatusRuntimeException e) {
      LOG.error(CALLING_GRPC_ERROR_TEXT, e);
      throw new RuntimeException(e);
    }
    List<PhotoOuterClass.Photo> listPhoto = response.getPhotosList();
    List<Photo> photos = listPhoto
        .stream()
        .map(typeConverter::fromGrpc)
        .toList();
    return new PageablePhoto(photos, response.getHasNext());
  }

  @Override
  public List<Like> getLikes(UUID photoId) {
    GetLikesRequest request;
    GetLikesResponse response;
    try {
      request = GetLikesRequest.newBuilder()
          .setPhotoId(photoId.toString())
          .build();
      response = photoServiceStub.getLikes(request);
    } catch (StatusRuntimeException e) {
      LOG.error(CALLING_GRPC_ERROR_TEXT, e);
      throw new RuntimeException(e);
    }

    return response.getLikesList()
        .stream()
        .map(typeConverter::fromGrpc)
        .toList();
  }

  @Override
  public List<Stat> getStat(List<String> users) {
    GetStatRequest request;
    GetStatResponse response;
    List<Username> usernamesProto = users.stream()
        .map(us -> Username.newBuilder().setUsername(us).build())
        .toList();
    try {
      request = GetStatRequest.newBuilder()
          .addAllUsername(usernamesProto)
          .build();

      response = photoServiceStub.getStat(request);
    } catch (StatusRuntimeException e) {
      LOG.error(CALLING_GRPC_ERROR_TEXT, e);
      throw new RuntimeException(e);
    }
    return response.getStatisticList()
        .stream()
        .map(typeConverter::fromGrpc)
        .toList();
  }

  @Override
  public String getCreatedPhotoUsernameByPhotoId(UUID photoId) {
    GetPhotoByIdRequest request;
    PhotoResponse response;
    try {
      request = GetPhotoByIdRequest.newBuilder()
          .setId(photoId.toString())
          .build();

      response = photoServiceStub.getPhoto(request);
    } catch (StatusRuntimeException e) {
      LOG.error(CALLING_GRPC_ERROR_TEXT, e);
      throw new RuntimeException(e);
    }

    return response.getUsername();
  }
}
