package project.qa.rangiffler.service.api;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;
import project.qa.rangiffler.data.LikeEntity;
import project.qa.rangiffler.data.PhotoEntity;
import project.qa.rangiffler.data.repository.LikeRepository;
import project.qa.rangiffler.data.repository.PhotoRepository;

@Component
public class PhotoServiceImpl implements PhotoService {

  private final PhotoRepository photoRepository;
  private final LikeRepository likeRepository;

  @Autowired
  public PhotoServiceImpl(PhotoRepository photoRepository,
      LikeRepository likeRepository) {
    this.photoRepository = photoRepository;
    this.likeRepository = likeRepository;
  }

  @Override
  public PhotoEntity addPhoto(String username, String src, String countryCode, String description) {
    PhotoEntity photoEntity = new PhotoEntity();
    photoEntity.setUsername(username);
    photoEntity.setPhoto(src);
    photoEntity.setCountryCode(countryCode);
    photoEntity.setDescription(description);
    photoEntity.setCreatedDate(LocalDateTime.now());
    return photoRepository.save(photoEntity);
  }

  @Override
  public PhotoEntity changePhoto(String id, String countryCode, String description) {
    PhotoEntity forChange = findById(id);
    forChange.setCountryCode(countryCode);
    forChange.setDescription(description);
    forChange.setCreatedDate(LocalDateTime.now());
    return photoRepository.save(forChange);
  }

  @Override
  public boolean deletePhoto(String id) {
    PhotoEntity forDelete = findById(id);
    List<LikeEntity> likes = findLikes(id);
    if (!likes.isEmpty()) {
      likeRepository.deleteAll(likes);
    }
    photoRepository.delete(forDelete);
    return true;
  }

  @Override
  public Slice<PhotoEntity> findPhotosByUsers(List<String> usernames, Pageable pageable) {
    return photoRepository.findByUsernameIn(usernames, pageable);
  }

  @Override
  public List<PhotoEntity> findPhotosByUsers(List<String> usernames) {
    return photoRepository.findByUsernameIn(usernames);
  }

  @Override
  public void changeLike(String username, String userId, String photoId) {
    UUID userUid = UUID.fromString(userId);
    UUID photoUid = UUID.fromString(photoId);
    Optional<LikeEntity> mayBeLike =
        likeRepository.findByUserIdAndUsernameAndPhotoId(
            userUid,
            username,
            photoUid);
    if (mayBeLike.isPresent()) {
      likeRepository.delete(mayBeLike.get());
    } else {
      PhotoEntity likedPhoto = photoRepository.findById(photoUid).orElseThrow();
      LikeEntity like = new LikeEntity();
      like.setUsername(username);
      like.setUserId(userUid);
      like.setCreatedDate(LocalDateTime.now());
      like.setPhoto(likedPhoto);
      likeRepository.save(like);
    }
  }

  @Override
  public PhotoEntity findById(String id) {
    return photoRepository.findById(UUID.fromString(id)).orElseThrow();
  }

  @Override
  public List<LikeEntity> findLikes(String photoId) {
    UUID photoUid = UUID.fromString(photoId);
    return likeRepository.findByPhotoId(photoUid).orElse(Collections.emptyList());
  }

  @Override
  public String findCountryCode(String photoId) {
    UUID photoUid = UUID.fromString(photoId);
    PhotoEntity photo = photoRepository.findById(photoUid).orElseThrow();
    return photo.getCountryCode();
  }

}
