package project.qa.rangiffler.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import project.qa.rangiffler.model.mutation.PhotoInput;
import project.qa.rangiffler.model.query.Country;
import project.qa.rangiffler.model.query.Feed;
import project.qa.rangiffler.model.query.Like;
import project.qa.rangiffler.model.query.Likes;
import project.qa.rangiffler.model.query.PageableObjects;
import project.qa.rangiffler.model.query.Photo;
import project.qa.rangiffler.model.query.Stat;
import project.qa.rangiffler.model.query.User;
import project.qa.rangiffler.service.api.GeoClient;
import project.qa.rangiffler.service.api.PhotoClient;
import project.qa.rangiffler.service.api.UserClient;

@Service
public class PhotoAggregatorService {

  private final UserClient userClient;
  private final PhotoClient photoClient;
  private final GeoClient geoClient;

  @Autowired
  public PhotoAggregatorService(UserClient userClient,
      PhotoClient photoClient, GeoClient geoClient) {
    this.userClient = userClient;
    this.photoClient = photoClient;
    this.geoClient = geoClient;
  }

  public Country findCountryBy(Photo photo) {
    return geoClient.findByCode(photo.contry().code());
  }

  public Likes findTotalLikes(Photo photo) {
    List<Like> likes = photoClient.getLikes(photo.id());
    return new Likes(likes.size(), likes);
  }

  public Slice<Photo> findPhotos(Feed feed, int page, int size) {
    final List<String> userNameList = new ArrayList<>();

    if (feed.withFriends()) {
      List<User> friends = userClient
          .friends(feed.username(), 0, 1000000, null)
          .getObjects();
      userNameList.addAll(friends
          .stream()
          .map(us -> us.username())
          .toList());
      userNameList.add(feed.username());
    } else {
      userNameList.add(feed.username());
    }
    PageableObjects<Photo> photos = photoClient.getPhotos(userNameList, size, page);
    return photos.getObjects().isEmpty() ?
        null :
        new SliceImpl<>(photos.getObjects(),
            PageRequest.of(page, size),
            photos.isHasNext());
  }

  public List<Stat> findStats(Feed feed) {
    if (feed.withFriends()) {
      List<User> friends = userClient
          .friends(feed.username(),0, 1000000, null)
          .getObjects();
      List<String> userNameList = friends
          .stream()
          .map(us-> us.username())
          .toList();
      userNameList.add(feed.username());
      return photoClient.getStat(userNameList);
    } else {
      return photoClient.getStat(List.of(feed.username()));
    }
  }

  public Photo savePhoto(String username, PhotoInput input) {
    return photoClient.addPhoto(input.src(),
        input.country().code(),
        input.description(),
        username);
  }

  public Photo changePhoto(String username, PhotoInput input) {
    return photoClient.changePhoto(input.id(),
        input.src(),
        input.country().code(),
        input.description(),
        username);
  }

  public Photo changeLike(String username, PhotoInput input) {
    User user = userClient.byUsername(username);
    return photoClient.changeLike(username,user.id(), input.id());
  }

  public boolean deletePhoto(String username, UUID photoId) {
    return photoClient.deletePhoto(photoId);
  }
}
