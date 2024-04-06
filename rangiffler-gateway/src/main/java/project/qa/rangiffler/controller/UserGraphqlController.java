package project.qa.rangiffler.controller;

import graphql.schema.DataFetchingEnvironment;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Controller;
import project.qa.rangiffler.model.query.Country;
import project.qa.rangiffler.model.query.User;

@Controller
public class UserGraphqlController {

  @SchemaMapping(typeName = "User", field = "friends")
  public Slice<User> friends(User user,
      @Argument int page,
      @Argument int size,
      @Argument @Nullable String searchQuery) {
    return null;
  }

  @SchemaMapping(typeName = "User", field = "incomeInvitations")
  public Slice<User> incomeInvitations (User user,
      @Argument int page,
      @Argument int size,
      @Argument @Nullable String searchQuery) {
    return null;
  }

  @SchemaMapping(typeName = "User", field = "outcomeInvitations")
  public Slice<User> outcomeInvitations (User user,
      @Argument int page,
      @Argument int size,
      @Argument @Nullable String searchQuery) {
    return null;
  }

  @QueryMapping
  public Slice<User> users(@AuthenticationPrincipal Jwt principal,
      @Argument int page,
      @Argument int size,
      @Argument @Nullable String searchQuery) {

    User user =
        new User(UUID.randomUUID(), "duck", "surname", "avatar",
            List.of(
                new User(UUID.randomUUID(),
                    "duck",
                    "surname",
                    "avatar",
                    null,
                    null,
                    null,
                    new Country("1", "2", "3"))),
            null,
            null,
            new Country("1", "2", "3")
        );

    User user1 =
        new User(UUID.randomUUID(), "barsik", "barsik", "barsik",
            List.of(
                new User(UUID.randomUUID(),
                    "barsik",
                    "barsik",
                    "barsik",
                    null,
                    null,
                    null,
                    new Country("1", "2", "3"))),
            null,
            null,
            new Country("1", "2", "3")
        );

    String username = principal.getClaim("sub");
    return new SliceImpl<>(List.of(user, user1), Pageable.ofSize(size), true);
  }

  @QueryMapping
  public User user(@AuthenticationPrincipal Jwt principal,
      @Nonnull DataFetchingEnvironment env) {
    String username = principal.getClaim("sub");
    User user =
        new User(UUID.randomUUID(), "duck", "surname", "avatar",
            List.of(
                new User(UUID.randomUUID(),
                    "duck",
                    "surname",
                    "avatar",
                    null,
                    null,
                    null,
                    new Country("1", "2", "3"))),
            null,
            null,
            new Country("1", "2", "3")
        );
    return user;
  }


}
