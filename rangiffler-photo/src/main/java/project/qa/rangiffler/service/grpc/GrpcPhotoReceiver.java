package project.qa.rangiffler.service.grpc;

import guru.qa.grpc.rangiffler.PhotoOuterClass.AddPhotoRequest;
import guru.qa.grpc.rangiffler.PhotoOuterClass.ChangeLikeRequest;
import guru.qa.grpc.rangiffler.PhotoOuterClass.ChangePhotoRequest;
import guru.qa.grpc.rangiffler.PhotoOuterClass.DeletePhotoRequest;
import guru.qa.grpc.rangiffler.PhotoOuterClass.DeletePhotoResponse;
import guru.qa.grpc.rangiffler.PhotoOuterClass.GetLikesRequest;
import guru.qa.grpc.rangiffler.PhotoOuterClass.GetLikesResponse;
import guru.qa.grpc.rangiffler.PhotoOuterClass.GetPhotoCountryCodeRequest;
import guru.qa.grpc.rangiffler.PhotoOuterClass.GetPhotoCountryCodeResponse;
import guru.qa.grpc.rangiffler.PhotoOuterClass.GetPhotosRequest;
import guru.qa.grpc.rangiffler.PhotoOuterClass.GetPhotosResponse;
import guru.qa.grpc.rangiffler.PhotoOuterClass.GetStatRequest;
import guru.qa.grpc.rangiffler.PhotoOuterClass.GetStatResponse;
import guru.qa.grpc.rangiffler.PhotoOuterClass.PhotoResponse;
import guru.qa.grpc.rangiffler.PhotoOuterClass.Statistic;
import guru.qa.grpc.rangiffler.PhotoOuterClass.Username;
import guru.qa.grpc.rangiffler.PhotoServiceGrpc;
import io.grpc.stub.StreamObserver;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import project.qa.rangiffler.data.LikeEntity;
import project.qa.rangiffler.data.PhotoEntity;
import project.qa.rangiffler.service.api.PhotoService;
import project.qa.rangiffler.service.grpc.utils.GrpcToEntityConverter;

@GrpcService
public class GrpcPhotoReceiver extends PhotoServiceGrpc.PhotoServiceImplBase {

  private final PhotoService photoService;

  @Autowired
  public GrpcPhotoReceiver(PhotoService photoService) {
    this.photoService = photoService;
  }

  @Override
  public void addPhoto(AddPhotoRequest request, StreamObserver<PhotoResponse> responseObserver) {
    PhotoEntity saved = photoService.addPhoto(request.getUsername(),
        request.getSrc(),
        request.getCountryCode(),
        request.getDescription());

    PhotoResponse response = GrpcToEntityConverter.toPhotoResponse(saved);

    responseObserver.onNext(response);
    responseObserver.onCompleted();

  }

  @Override
  public void changePhoto(ChangePhotoRequest request,
      StreamObserver<PhotoResponse> responseObserver) {
    PhotoEntity changed = photoService.changePhoto(request.getId(),
        request.getCountryCode(),
        request.getDescription());

    PhotoResponse response = GrpcToEntityConverter.toPhotoResponse(changed);

    responseObserver.onNext(response);
    responseObserver.onCompleted();

  }

  @Override
  public void deletePhoto(DeletePhotoRequest request,
      StreamObserver<DeletePhotoResponse> responseObserver) {
    boolean isDeleted = photoService.deletePhoto(request.getId());
    DeletePhotoResponse response = DeletePhotoResponse.newBuilder()
        .setIsSuccess(isDeleted)
        .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void getPhotos(GetPhotosRequest request,
      StreamObserver<GetPhotosResponse> responseObserver) {
    List<String> users = request
        .getUsernameList()
        .stream()
        .map(Username::getUsername)
        .toList();

    Slice<PhotoEntity> photos =
        photoService.findPhotosByUsers(
            users,
            PageRequest.of(request.getPageInfo().getPage(), request.getPageInfo().getSize()));

    GetPhotosResponse response = GetPhotosResponse.newBuilder()
        .addAllPhotos(GrpcToEntityConverter.toGrpcListPhotos(photos.getContent()))
        .setHasNext(photos.hasNext())
        .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void changeLike(ChangeLikeRequest request,
      StreamObserver<PhotoResponse> responseObserver) {
    photoService.changeLike(request.getUsername(),
        request.getUserId(),
        request.getPhotoId());

    PhotoEntity photo = photoService.findById(request.getPhotoId());

    PhotoResponse response = GrpcToEntityConverter.toPhotoResponse(photo);

    responseObserver.onNext(response);
    responseObserver.onCompleted();

  }

  @Override
  public void getLikes(GetLikesRequest request, StreamObserver<GetLikesResponse> responseObserver) {
    GetLikesResponse response;
    List<LikeEntity> likes = photoService.findLikes(request.getPhotoId());
    if (likes.isEmpty()) {
      response = GetLikesResponse.getDefaultInstance();
    } else {
      response = GetLikesResponse.newBuilder()
          .setPhotoId(request.getPhotoId())
          .addAllLikes(GrpcToEntityConverter.toGrpcListLikes(likes))
          .build();
    }

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void getPhotoCountryCode(GetPhotoCountryCodeRequest request,
      StreamObserver<GetPhotoCountryCodeResponse> responseObserver) {
    GetPhotoCountryCodeResponse response;

    String countryCode = photoService.findCountryCode(request.getPhotoId());

    if (Objects.isNull(countryCode)) {
      response = GetPhotoCountryCodeResponse.getDefaultInstance();
    } else {
      response = GetPhotoCountryCodeResponse.newBuilder()
          .setCode(countryCode)
          .build();
    }
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void getStat(GetStatRequest request, StreamObserver<GetStatResponse> responseObserver) {
    List<String> users = request.getUsernameList().stream().map(Username::getUsername).toList();
    List<PhotoEntity> photos = photoService.findPhotosByUsers(users);

    List<Statistic> statistics = new ArrayList<>();
    Map<String, Long> countMap = photos
        .stream()
        .collect(Collectors.groupingBy(PhotoEntity::getCountryCode, Collectors.counting()));

    for (Map.Entry<String, Long> entry : countMap.entrySet()) {
      statistics.add(Statistic.newBuilder()
          .setCountryCode(entry.getKey())
          .setCount(Math.toIntExact(entry.getValue()))
          .build());
    }

    GetStatResponse response = GetStatResponse.newBuilder()
        .addAllStatistic(statistics)
        .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

}
