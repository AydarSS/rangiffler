package project.qa.rangiffler.service.api;

import java.util.List;
import project.qa.rangiffler.model.query.Country;
import project.qa.rangiffler.model.query.Friendship;
import project.qa.rangiffler.model.query.PageableObjects;
import project.qa.rangiffler.model.query.User;

public interface UserClient {

  User byUsername(String username);

  PageableObjects<User> allUsers(String username, int page, int size, String searchQuery);

  PageableObjects<User> friends(String username, int page, int size, String searchQuery);

  PageableObjects<User> incomeInvitations(String username, int page, int size, String searchQuery);

  PageableObjects<User> outcomeInvitations(String username, int page, int size, String searchQuery);

  User updateUser(User user);

  User friendshipAction(Friendship friendship);

}
