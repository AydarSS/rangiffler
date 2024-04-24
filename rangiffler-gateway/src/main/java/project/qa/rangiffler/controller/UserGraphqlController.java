package project.qa.rangiffler.controller;

import graphql.schema.DataFetchingEnvironment;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import project.qa.rangiffler.model.query.PageableObjects;
import project.qa.rangiffler.model.query.User;
import project.qa.rangiffler.service.api.UserClient;

@Controller
public class UserGraphqlController {

  private final UserClient userClient;
  private final String STRING_EMPTY = "";

  @Autowired
  public UserGraphqlController(UserClient userClient) {
    this.userClient = userClient;
  }

  @SchemaMapping(typeName = "User", field = "friends")
  public Slice<User> friends(User user,
      @Argument int page,
      @Argument int size,
      @Argument @Nullable String searchQuery) {
    PageableObjects pageableUsers = userClient.friends(user.username(), page, size, searchQuery);
    return createSlice(page, size, pageableUsers);
  }

  @SchemaMapping(typeName = "User", field = "incomeInvitations")
  public Slice<User> incomeInvitations(User user,
      @Argument int page,
      @Argument int size,
      @Argument @Nullable String searchQuery) {
    PageableObjects pageableUsers = userClient.incomeInvitations(user.username(), page, size,
        searchQuery);
    return createSlice(page, size, pageableUsers);
  }

  @SchemaMapping(typeName = "User", field = "outcomeInvitations")
  public Slice<User> outcomeInvitations(User user,
      @Argument int page,
      @Argument int size,
      @Argument @Nullable String searchQuery) {
    PageableObjects pageableUsers = userClient.outcomeInvitations(user.username(), page, size,
        searchQuery);
    return createSlice(page, size, pageableUsers);
  }

  @QueryMapping
  public Slice<User> users(@AuthenticationPrincipal Jwt principal,
      @Argument int page,
      @Argument int size,
      @Argument @Nullable String searchQuery) {
    String username = principal.getClaim("sub");
    PageableObjects pageableUsers = userClient.allUsers(username, page, size, searchQuery);
    return createSlice(page, size, pageableUsers);
  }

  @QueryMapping
  public User user(@AuthenticationPrincipal Jwt principal, @Nonnull DataFetchingEnvironment env) {
    String username = principal.getClaim("sub");
    return userClient.byUsername(username);
  }

  private Slice<User> createSlice(int page, int size, PageableObjects pageableObjects) {
    return pageableObjects.getObjects().isEmpty() ?
        new SliceImpl<>(Collections.emptyList()) :
        new SliceImpl<User>(pageableObjects.getObjects(), PageRequest.of(page, size),
            pageableObjects.isHasNext());
  }

}
