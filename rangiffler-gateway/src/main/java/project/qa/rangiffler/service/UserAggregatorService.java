package project.qa.rangiffler.service;

import jakarta.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;
import org.springframework.stereotype.Service;
import project.qa.rangiffler.model.query.Country;
import project.qa.rangiffler.model.query.Friendship;
import project.qa.rangiffler.model.query.PageableObjects;
import project.qa.rangiffler.model.query.PageableUsers;
import project.qa.rangiffler.model.query.User;
import project.qa.rangiffler.service.api.GeoClient;
import project.qa.rangiffler.service.api.GrpcGeoClient;
import project.qa.rangiffler.service.api.GrpcUserClient;
import project.qa.rangiffler.service.api.UserClient;

@Service
public class UserAggregatorService {

  private final UserClient userClient;
  private final GeoClient geoClient;

  public UserAggregatorService() {
    this.userClient = new GrpcUserClient();
    this.geoClient = new GrpcGeoClient();
  }

  public PageableObjects<User> friends(String username, int page, int size, @Nullable String searchQuery) {
    return findUsers(
        () -> userClient.friends(username, page, size, searchQuery));
  }

  public PageableObjects<User> incomeInvitations(String username, int page, int size, @Nullable String searchQuery) {
    return findUsers(
        () ->  userClient.incomeInvitations(username, page, size, searchQuery));
  }

  public PageableObjects<User> outcomeInvitations(String username, int page, int size, @Nullable String searchQuery) {
    return findUsers(
        () ->  userClient.outcomeInvitations(username, page, size, searchQuery));
  }

  public PageableObjects<User> allUsers(String username, int page, int size, @Nullable String searchQuery) {
    return findUsers(
          () ->  userClient.allUsers(username, page, size, searchQuery));
  }

  public User byUsername(String username) {
    User findedUser = userClient.byUsername(username);
    return withCountry(findedUser);
  }

  public User friendshipAction(Friendship friendship){
    User user = userClient.friendshipAction(friendship);
    return withCountry(user);
  }

  public User updateUser(User user) {
    User updates = userClient.updateUser(user);
    return withCountry(updates);
  }

  private PageableObjects<User> findUsers(Supplier<PageableObjects<User>> usersRequest) {
    PageableObjects<User> pageableUsers = usersRequest.get();

    List<User> users = pageableUsers
        .getObjects()
        .stream()
        .map(this::withCountry)
        .toList();

    return new PageableUsers(
        users,
        pageableUsers.isHasNext());
  }

  private User withCountry(User user) {
    Country country = geoClient.findById(user.country().id());
    return user.withCountry(country);
  }
}
