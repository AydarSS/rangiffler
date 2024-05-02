package project.qa.rangiffler.service;

import com.google.protobuf.Timestamp;
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
import guru.qa.grpc.rangiffler.PhotoOuterClass.Photo;
import guru.qa.grpc.rangiffler.PhotoOuterClass.PhotoResponse;
import guru.qa.grpc.rangiffler.PhotoServiceGrpc;
import io.grpc.stub.StreamObserver;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import project.qa.rangiffler.data.PhotoEntity;
import project.qa.rangiffler.data.repository.LikeRepository;
import project.qa.rangiffler.data.repository.PhotoRepository;

@GrpcService
public class GrpcPhotoService extends PhotoServiceGrpc.PhotoServiceImplBase {

  private final PhotoRepository photoRepository;
  private final LikeRepository likeRepository;

  @Autowired
  public GrpcPhotoService(PhotoRepository photoRepository,
      LikeRepository likeRepository) {
    this.photoRepository = photoRepository;
    this.likeRepository = likeRepository;
  }

  @Override
  public void addPhoto(AddPhotoRequest request, StreamObserver<PhotoResponse> responseObserver) {
    PhotoEntity photoEntity = new PhotoEntity();
    photoEntity.setUsername(request.getUsername());
    photoEntity.setPhoto(request.getSrc());
    photoEntity.setCountryCode(request.getCountryCode());
    photoEntity.setDescription(request.getDescription());
    photoEntity.setCreatedDate(LocalDateTime.now());
    PhotoEntity saved = photoRepository.save(photoEntity);

    PhotoResponse response = PhotoResponse.newBuilder()
        .setId(saved.getId().toString())
        .setUsername(saved.getUsername())
        .setSrc(saved.getPhoto())
        .setCountryCode(saved.getCountryCode())
        .setCreatedDate(convertLocalDateTimeToTimestamp(saved.getCreatedDate()))
        .setDescription(saved.getDescription())
        .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();

  }

  @Override
  public void changePhoto(ChangePhotoRequest request,
      StreamObserver<PhotoResponse> responseObserver) {
    Optional<PhotoEntity> photoEntityOptional = photoRepository.findById(
        UUID.fromString(request.getId()));
    if (!photoEntityOptional.isPresent()) {
      //TODO
    }
    PhotoEntity photoEntityForChange = photoEntityOptional.get();

    photoEntityForChange.setPhoto(request.getSrc());
    photoEntityForChange.setCountryCode(request.getCountryCode());
    photoEntityForChange.setDescription(request.getDescription());
    photoEntityForChange.setCreatedDate(LocalDateTime.now());

    PhotoEntity saved = photoRepository.save(photoEntityForChange);

    PhotoResponse response = PhotoResponse.newBuilder()
        .setId(saved.getId().toString())
        .setUsername(saved.getUsername())
        .setSrc(saved.getPhoto())
        .setCountryCode(saved.getCountryCode())
        .setCreatedDate(convertLocalDateTimeToTimestamp(saved.getCreatedDate()))
        .setDescription(saved.getDescription())
        .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();

  }

  @Override
  public void deletePhoto(DeletePhotoRequest request,
      StreamObserver<DeletePhotoResponse> responseObserver) {
    Optional<PhotoEntity> photoEntityOptional = photoRepository.findById(
        UUID.fromString(request.getId()));
    if (!photoEntityOptional.isPresent()) {
      //TODO
    }
    PhotoEntity photoEntityForDelete = photoEntityOptional.get();
    photoRepository.delete(photoEntityForDelete);
    DeletePhotoResponse response = DeletePhotoResponse.newBuilder()
        .setIsSuccess(true)
        .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void getPhotos(GetPhotosRequest request,
      StreamObserver<GetPhotosResponse> responseObserver) {
    Slice<PhotoEntity> photos = photoRepository.findByUsernameIn(
        request.getUsernameList(),
        PageRequest.of(request.getPageInfo().getPage(), request.getPageInfo().getSize()));
    GetPhotosResponse response = GetPhotosResponse.newBuilder()
        .addAllPhotos(toGrpcList(photos.getContent()))
        .setHasNext(photos.hasNext())
        .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void changeLike(ChangeLikeRequest request,
      StreamObserver<PhotoResponse> responseObserver) {
    super.changeLike(request, responseObserver);
  }

  @Override
  public void getLikes(GetLikesRequest request, StreamObserver<GetLikesResponse> responseObserver) {
    super.getLikes(request, responseObserver);
  }

  @Override
  public void getPhotoCountryCode(GetPhotoCountryCodeRequest request,
      StreamObserver<GetPhotoCountryCodeResponse> responseObserver) {
    super.getPhotoCountryCode(request, responseObserver);
  }

  @Override
  public void getStat(GetStatRequest request, StreamObserver<GetStatResponse> responseObserver) {
    super.getStat(request, responseObserver);
  }

  private Timestamp convertLocalDateTimeToTimestamp(LocalDateTime localDateTime) {
    Instant instant = localDateTime.toInstant(ZoneOffset.UTC);

    Timestamp result = Timestamp.newBuilder()
        .setSeconds(instant.getEpochSecond())
        .setNanos(instant.getNano())
        .build();

    return result;
  }

  private List<Photo> toGrpcList(List<PhotoEntity> photos) {
    return photos.stream()
        .map(this::toGrpc)
        .toList();
  }

  private Photo toGrpc(PhotoEntity photo) {
    return Photo.newBuilder()
        .setId(photo.getId().toString())
        .setSrc(photo.getPhoto())
        .setUsername(photo.getUsername())
        .setCountryCode(photo.getCountryCode())
        .setDescription(photo.getDescription())
        .setCreatedDate(convertLocalDateTimeToTimestamp(photo.getCreatedDate()))
        .build();
  }

}
