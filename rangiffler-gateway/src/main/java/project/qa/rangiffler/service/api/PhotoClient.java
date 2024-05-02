package project.qa.rangiffler.service.api;

import java.util.List;
import java.util.UUID;
import project.qa.rangiffler.model.query.Like;
import project.qa.rangiffler.model.query.PageableObjects;
import project.qa.rangiffler.model.query.Photo;
import project.qa.rangiffler.model.query.Stat;

public interface PhotoClient {

  Photo addPhoto(String src, String countryCode, String description, String username);

  Photo changeLike(String username, UUID userID, UUID photoId);

  Photo changePhoto(UUID photoId, String src, String countryCode, String description,
      String username);

  boolean deletePhoto(UUID photoId);

  PageableObjects<Photo> getPhotos(List<String> username, int size, int page);

  List<Like> getLikes(UUID photoId);

  List<Stat> getStat(List<String> usernames);


}
