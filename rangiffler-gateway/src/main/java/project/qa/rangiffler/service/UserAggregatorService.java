package project.qa.rangiffler.service;

import io.grpc.Status.Code;
import io.grpc.StatusRuntimeException;
import jakarta.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.qa.rangiffler.model.query.Country;
import project.qa.rangiffler.model.query.Friendship;
import project.qa.rangiffler.model.query.PageableObjects;
import project.qa.rangiffler.model.query.PageableUsers;
import project.qa.rangiffler.model.query.User;
import project.qa.rangiffler.service.api.GeoClient;
import project.qa.rangiffler.service.api.UserClient;

@Service
public class UserAggregatorService {

  private final UserClient userClient;
  private final GeoClient geoClient;
  private static final String STRING_EMPTY = "";

  @Autowired
  public UserAggregatorService(UserClient userClient,
      GeoClient geoClient) {
    this.userClient = userClient;
    this.geoClient = geoClient;
  }

  public PageableObjects<User> friends(String username, int page, int size,
      @Nullable String searchQuery) {
    return findUsers(
        () -> userClient.friends(username, page, size, searchQuery));
  }

  public PageableObjects<User> incomeInvitations(String username, int page, int size,
      @Nullable String searchQuery) {
    return findUsers(
        () -> userClient.incomeInvitations(username, page, size, searchQuery));
  }

  public PageableObjects<User> outcomeInvitations(String username, int page, int size,
      @Nullable String searchQuery) {
    return findUsers(
        () -> userClient.outcomeInvitations(username, page, size, searchQuery));
  }

  public PageableObjects<User> allUsers(String username, int page, int size,
      @Nullable String searchQuery) {
    return findUsers(
        () -> userClient.allUsers(username, page, size, searchQuery));
  }

  public User byUsername(String username) {
    User findedUser = userClient.byUsername(username);
    return withCountryIfExists(findedUser);
  }

  public User friendshipAction(Friendship friendship) {
    User user = userClient.friendshipAction(friendship);
    return withCountryIfExists(user);
  }

  public User updateUser(User user) {
    User updates;
    if (Objects.nonNull(user.country()) && !user.country().code().equals(STRING_EMPTY)) {
      Country country = geoClient.findByCode(user.country().code());
      updates = userClient.updateUser(user.withCountry(country));
    } else {
      updates = userClient.updateUser(user);
    }
    return withCountryIfExists(updates);
  }

  private PageableObjects<User> findUsers(Supplier<PageableObjects<User>> usersRequest) {
    PageableObjects<User> pageableUsers = usersRequest.get();

    List<User> users = pageableUsers
        .getObjects()
        .stream()
        .map(this::withCountryIfExists)
        .toList();

    return new PageableUsers(
        users,
        pageableUsers.isHasNext());
  }

  private User withCountryIfExists(User user) {
    Country country;
    if (Objects.nonNull(user.country())) {
      country = countryByIdOrEmpty(user.country().id());
      return user.withCountry(country);
    } else {
      return user.withCountry(Country.emptyContry());
    }
  }

  private Country countryByIdOrEmpty(UUID countryId) {
    Country country;
    try {
      country = geoClient.findById(countryId);
      return country;
    } catch (StatusRuntimeException ex) {
      if (ex.getStatus().getCode().equals(Code.INVALID_ARGUMENT)) {
        return Country.emptyContry();
      } else {
        throw new RuntimeException(ex);
      }
    }
  }
}
