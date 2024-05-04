package project.qa.rangiffler.service.api;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import project.qa.rangiffler.data.LikeEntity;
import project.qa.rangiffler.data.PhotoEntity;

public interface PhotoService {

  PhotoEntity addPhoto(String username, String src, String countryCode, String description);

  PhotoEntity changePhoto(String id, String countryCode, String description);

  boolean deletePhoto(String id);

  Slice<PhotoEntity> findPhotosByUsers(List<String> usernames, Pageable pageable);

  List<PhotoEntity> findPhotosByUsers(List<String> usernames);

  void changeLike(String username, String userId, String photoId);

  PhotoEntity findById(String id);

  List<LikeEntity> findLikes(String photoId);

  String findCountryCode(String photoId);
}
