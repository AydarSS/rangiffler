package project.qa.rangiffler.service.api;

import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import project.qa.rangiffler.data.FriendshipEntity;
import project.qa.rangiffler.data.UserEntity;

public interface UserDataService {

  UserEntity getCurrentUserByUsername(String username);

  Slice<UserEntity> findAllUsers(String username, Pageable pageable, String searchQuery);

  UserEntity findByUsername(String username);

  Slice<UserEntity> findFriends(String username, Pageable pageable, String searchQuery);

  Slice<UserEntity> findIncomeInvitations(String username, Pageable pageable, String searchQuery);

  Slice<UserEntity> findOutcomeInvitations(String username, Pageable pageable, String searchQuery);

  void addFriendshipRequest(String linkedUserId, String currentUsername);

  void acceptFriendshipRequest(String linkedUserId, String currentUsername);

  void rejectFriendshipRequest(String linkedUserId, String currentUsername);

  void deleteFriendshipRequest(String linkedUserId, String currentUsername);

  Optional<FriendshipEntity> findFrienship(UserEntity requester, UserEntity addresse);

  UserEntity updateUser(String username, String firstname, String surname, String avatar,
      String countryId);


}
