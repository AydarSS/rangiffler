package project.qa.rangiffler.controller;

import graphql.schema.DataFetchingEnvironment;
import graphql.schema.SelectedField;
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
import project.qa.rangiffler.ex.ToManySubQueriesException;
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
  }

  @SchemaMapping(typeName = "Photo", field = "likes")
  public Likes getTotalLikes(Photo photo) {
    return photoService.findTotalLikes(photo);
  }

  @SchemaMapping(typeName = "Feed", field = "photos")
  public Slice<Photo> getPhotos(Feed feed,
      @Argument int page,
      @Argument int size) {
    return photoService.findPhotos(feed, page, size);
  }

  @SchemaMapping(typeName = "Feed", field = "stat")
  public List<Stat> getStats(Feed feed) {
    return photoService.findStats(feed);
  }

  @QueryMapping
  public Feed feed(@AuthenticationPrincipal Jwt principal,
      @Argument boolean withFriends, @Nonnull DataFetchingEnvironment env) {
    checkSubQueries(env, 2, "photos");
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
  }

  @MutationMapping
  @ResponseStatus(HttpStatus.CREATED)
  public boolean deletePhoto(@AuthenticationPrincipal Jwt principal, @Argument UUID id) {
    String username = principal.getClaim("sub");
    return photoService.deletePhoto(username, id);
  }

  private void checkSubQueries(@Nonnull DataFetchingEnvironment env, int depth, @Nonnull String... queryKeys) {
    for (String queryKey : queryKeys) {
      List<SelectedField> selectors = env.getSelectionSet().getFieldsGroupedByResultKey().get(queryKey);
      if (selectors != null && selectors.size() > depth) {
        throw new ToManySubQueriesException("Can`t fetch over 2 " + queryKey + " sub-queries");
      }
    }
  }
}
