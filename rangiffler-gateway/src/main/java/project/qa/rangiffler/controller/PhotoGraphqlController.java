package project.qa.rangiffler.controller;

import graphql.schema.DataFetchingEnvironment;
import jakarta.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseStatus;
import project.qa.rangiffler.model.mutation.PhotoInput;
import project.qa.rangiffler.model.query.Country;
import project.qa.rangiffler.model.query.Feed;
import project.qa.rangiffler.model.query.Likes;
import project.qa.rangiffler.model.query.Photo;
import project.qa.rangiffler.model.query.Stat;
import project.qa.rangiffler.service.PhotoAggregatorService;

@Controller
public class PhotoGraphqlController {

  private final PhotoAggregatorService photoService;

  @Autowired
  public PhotoGraphqlController(PhotoAggregatorService photoService) {
    this.photoService = photoService;
  }

  @SchemaMapping(typeName = "Photo", field = "country")
  public Country getCountry(Photo photo) {
    return photoService.findCountryBy(photo);
//    return new Country("12", "RU", "Russia");
  }

  @SchemaMapping(typeName = "Photo", field = "likes")
  public Likes getTotalLikes(Photo photo) {
    return photoService.findTotalLikes(photo);
   /* return new Likes(3, List.of(
        new Like(UUID.fromString("ee883cd2-cd8c-4502-a394-56cd860c1500"), "duck", new Date())));*/
  }

  @SchemaMapping(typeName = "Feed", field = "photos")
  public Slice<Photo> getPhotos(Feed feed,
      @Argument int page,
      @Argument int size) {
    return photoService.findPhotos(feed, page, size);
   /* List<Photo> photos = List.of(
        new Photo(UUID.randomUUID(),
            "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAyAAAAI7CAIAAACbdGDOAQ8LY2zmRqhSZwk2yoTN47Ju56dP/XkLfGyctvs3b1Xmp0/qfWqq5tZ72GY2hxIiVJnn5UI9X1brfXMS0rtBJFSezucQUHVuQuHWezsuuswwh3HbOn1ExiIx6hQjQIIifZg/k0iz6GENAIxdvF",
            null,
            "descr1",
            new Date(),
            null),
        new Photo(UUID.randomUUID(),
            "data:image/png;base64,dfsds/XkLfGyctvs3b1Xmp0/qfWqq5tZ72GY2hxIiVJnn5UI9X1brfXMS0rtBJFSezucQUHVuQuHWezsuuswwh3HbOn1ExiIx6hQjQIIifZg/k0iz6GENAIxdvF",
            null,
            "99999",
            new Date(),
            null));
    return new SliceImpl<>(photos,
        PageRequest.of(page, size),
        true);*/
  }

  @SchemaMapping(typeName = "Feed", field = "stat")
  public List<Stat> getStats(Feed feed) {
    return photoService.findStats(feed);
    //return List.of(new Stat(3, new Country(UUID.randomUUID(), "", "ru", "")));
  }

  @QueryMapping
  public Feed feed(@AuthenticationPrincipal Jwt principal,
      @Argument boolean withFriends, @Nonnull DataFetchingEnvironment env) {
    String username = principal.getClaim("sub");
    return new Feed(username, withFriends,
        new ArrayList<>(),
        new ArrayList<>());
  }

  @MutationMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Photo photo(@AuthenticationPrincipal Jwt principal, @Argument PhotoInput input) {
    String username = principal.getClaim("sub");
    if (Objects.isNull(input.id())) {
      return photoService.savePhoto(username, input);
    } else if (Objects.nonNull(input.like())) {
      return photoService.changeLike(username, input);
    } else {
      return photoService.changePhoto(username, input);
    }

  /*  return new Photo(UUID.randomUUID(), "", new Country("", "", ""), "descr",
        new Date(), null);*/
  }

  @MutationMapping
  @ResponseStatus(HttpStatus.CREATED)
  public boolean deletePhoto(@AuthenticationPrincipal Jwt principal, @Argument UUID id) {
    String username = principal.getClaim("sub");
    return photoService.deletePhoto(username, id);
  }

}
