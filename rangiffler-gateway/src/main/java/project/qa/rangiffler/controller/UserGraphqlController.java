package project.qa.rangiffler.controller;

import graphql.schema.DataFetchingEnvironment;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseStatus;
import project.qa.rangiffler.model.mutation.FriendshipInput;
import project.qa.rangiffler.model.mutation.UserInput;
import project.qa.rangiffler.model.query.Country;
import project.qa.rangiffler.model.query.Friendship;
import project.qa.rangiffler.model.query.PageableObjects;
import project.qa.rangiffler.model.query.User;
import project.qa.rangiffler.service.UserAggregatorService;

@Controller
public class UserGraphqlController {

  private final UserAggregatorService userAggregatorService;

  @Autowired
  public UserGraphqlController(UserAggregatorService userAggregatorService) {
    this.userAggregatorService = userAggregatorService;
  }

  @SchemaMapping(typeName = "User", field = "friends")
  public Slice<User> friends(User user,
      @Argument int page,
      @Argument int size,
      @Argument @Nullable String searchQuery) {
    PageableObjects pageableUsers = userAggregatorService.friends(user.username(), page, size,
        searchQuery);
    return createSlice(page, size, pageableUsers);
  }

  @SchemaMapping(typeName = "User", field = "incomeInvitations")
  public Slice<User> incomeInvitations(User user,
      @Argument int page,
      @Argument int size,
      @Argument @Nullable String searchQuery) {
    PageableObjects pageableUsers = userAggregatorService.incomeInvitations(user.username(), page,
        size,
        searchQuery);
    return createSlice(page, size, pageableUsers);
  }

  @SchemaMapping(typeName = "User", field = "location")
  public Country location(User user) {
    return user.country();
  }

  @SchemaMapping(typeName = "User", field = "outcomeInvitations")
  public Slice<User> outcomeInvitations(User user,
      @Argument int page,
      @Argument int size,
      @Argument @Nullable String searchQuery) {
    PageableObjects pageableUsers = userAggregatorService.outcomeInvitations(user.username(), page,
        size,
        searchQuery);
    return createSlice(page, size, pageableUsers);
  }

  @QueryMapping
  public Slice<User> users(@AuthenticationPrincipal Jwt principal,
      @Argument int page,
      @Argument int size,
      @Argument @Nullable String searchQuery) {
    String username = principal.getClaim("sub");
    PageableObjects pageableUsers = userAggregatorService.allUsers(username, page, size,
        searchQuery);
    return createSlice(page, size, pageableUsers);
  }

  @QueryMapping
  public User user(@AuthenticationPrincipal Jwt principal, @Nonnull DataFetchingEnvironment env) {
    String username = principal.getClaim("sub");
    return userAggregatorService.byUsername(username);
  }

  @MutationMapping
  @ResponseStatus(HttpStatus.CREATED)
  public User friendship(@AuthenticationPrincipal Jwt principal, @Argument FriendshipInput input) {
    String username = principal.getClaim("sub");
    return userAggregatorService.friendshipAction(Friendship.fromFriendshipInput(input, username));
  }

  @MutationMapping
  @ResponseStatus(HttpStatus.CREATED)
  public User user(@AuthenticationPrincipal Jwt principal, @Argument UserInput input) {
    String username = principal.getClaim("sub");
    return userAggregatorService.updateUser(User.fromUserInput(input, username));
  }

  private Slice<User> createSlice(int page, int size, PageableObjects pageableObjects) {
    return pageableObjects.getObjects().isEmpty() ?
        null :
        new SliceImpl<User>(pageableObjects.getObjects(),
            PageRequest.of(page, size),
            pageableObjects.isHasNext());
  }

}
