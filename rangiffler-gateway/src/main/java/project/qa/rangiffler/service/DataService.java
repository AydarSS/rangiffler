package project.qa.rangiffler.service;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Slice;
import project.qa.rangiffler.model.mutation.PhotoInput;
import project.qa.rangiffler.model.query.Country;
import project.qa.rangiffler.model.query.Feed;
import project.qa.rangiffler.model.query.Likes;
import project.qa.rangiffler.model.query.Photo;
import project.qa.rangiffler.model.query.Stat;

public interface DataService {

  Country findCountryBy(Photo photo);

  Likes findTotalLikes(Photo photo);

  Slice<Photo> findPhotos(Feed feed, int page, int size);

  List<Stat> findStats(Feed feed);

  Photo savePhoto(String username, PhotoInput input);

  Photo changePhoto(String username, PhotoInput input);

  Photo changeLike(String username, PhotoInput input);

  boolean deletePhoto(String username, UUID photoId);

}
