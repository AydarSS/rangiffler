package project.qa.rangiffler.service.api;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import project.qa.rangiffler.data.FriendshipEntity;
import project.qa.rangiffler.data.FriendshipStatus;
import project.qa.rangiffler.data.UserEntity;
import project.qa.rangiffler.data.repository.FriendshipRepository;
import project.qa.rangiffler.data.repository.UserRepository;
import project.qa.rangiffler.ex.ResourceNotFoundException;

@Component
public class UserDataServiceImpl implements UserDataService {

  private final UserRepository userRepository;
  private final FriendshipRepository friendshipRepository;
  private final String STRING_EMPTY = "";

  @Autowired
  public UserDataServiceImpl(UserRepository userRepository,
      FriendshipRepository friendshipRepository) {
    this.userRepository = userRepository;
    this.friendshipRepository = friendshipRepository;
  }

  @Override
  @Transactional(readOnly = true)
  public UserEntity getCurrentUserByUsername(String username) {
    return userRepository.findByUsername(username)
        .orElseThrow(
            () -> new ResourceNotFoundException(String.format("User %s not found", username)));
  }

  @Override
  @Transactional(readOnly = true)
  public Slice<UserEntity> findAllUsers(String username, Pageable pageable, String searchQuery) {
    if (isEmpty(searchQuery)) {
      return userRepository.findByUsernameNot(
          username,
          pageable);
    }
    return userRepository.findByUsernameNotAndSearchQuery(
        username,
        pageable,
        searchQuery);
  }

  @Override
  @Transactional(readOnly = true)
  public UserEntity findByUsername(String username) {
    return userRepository.findByUsername(username)
        .orElseThrow(
            () -> new ResourceNotFoundException(String.format("User %s not found", username)));
  }

  @Override
  @Transactional(readOnly = true)
  public Slice<UserEntity> findFriends(String username, Pageable pageable, String searchQuery) {
    UserEntity current = findByUsername(username);
    if (isEmpty(searchQuery) && isPageableIsNotExistsIn(pageable)) {
      return userRepository.findFriends(current);
    } else if (isEmpty(searchQuery)) {
      return userRepository.findFriends(current, pageable);
    } else if (isPageableIsNotExistsIn(pageable)) {
      return userRepository.findFriends(current, searchQuery);
    } else {
      return userRepository.findFriends(current, pageable, searchQuery);
    }
  }

  @Override
  @Transactional(readOnly = true)
  public Slice<UserEntity> findIncomeInvitations(String username, Pageable pageable,
      String searchQuery) {
    UserEntity current = findByUsername(username);
    if (isEmpty(searchQuery) && isPageableIsNotExistsIn(pageable)) {
      return userRepository.findIncomeInvitations(current);
    } else if (isEmpty(searchQuery)) {
      return userRepository.findIncomeInvitations(current, pageable);
    } else if (isPageableIsNotExistsIn(pageable)) {
      return userRepository.findIncomeInvitations(current, searchQuery);
    } else {
      return userRepository.findIncomeInvitations(current, pageable, searchQuery);
    }
  }

  @Override
  @Transactional(readOnly = true)
  public Slice<UserEntity> findOutcomeInvitations(String username, Pageable pageable,
      String searchQuery) {
    UserEntity current = findByUsername(username);
    if (isEmpty(searchQuery) && isPageableIsNotExistsIn(pageable)) {
      return userRepository.findOutcomeInvitations(current);
    } else if (isEmpty(searchQuery)) {
      return userRepository.findOutcomeInvitations(current, pageable);
    } else if (isPageableIsNotExistsIn(pageable)) {
      return userRepository.findOutcomeInvitations(current, searchQuery);
    } else {
      return userRepository.findOutcomeInvitations(current, pageable, searchQuery);
    }
  }

  @Override
  @Transactional
  public void addFriendshipRequest(String linkedUserId, String currentUsername) {
    UserEntity linked = findById(linkedUserId);
    UserEntity currentUser = findByUsername(currentUsername);
    FriendshipEntity friendship = new FriendshipEntity();

    friendship.setRequester(currentUser);
    friendship.setAddressee(linked);
    friendship.setStatus(FriendshipStatus.PENDING);
    friendship.setCreatedDate(LocalDateTime.now());

    friendshipRepository.save(friendship);
  }

  @Override
  @Transactional
  public void acceptFriendshipRequest(String linkedUserId, String currentUsername) {
    UserEntity linked = findById(linkedUserId);
    UserEntity currentUser = findByUsername(currentUsername);
    FriendshipEntity friendship = findFriendship(linked, currentUser);

    friendship.setStatus(FriendshipStatus.ACCEPTED);
    friendshipRepository.save(friendship);

    FriendshipEntity friendshipEntity = new FriendshipEntity();
    friendshipEntity.setRequester(currentUser);
    friendshipEntity.setAddressee(linked);

    friendshipEntity.setStatus(FriendshipStatus.ACCEPTED);
    friendshipEntity.setCreatedDate(LocalDateTime.now());
    friendshipRepository.save(friendshipEntity);
  }

  @Override
  @Transactional
  public void rejectFriendshipRequest(String linkedUserId, String currentUsername) {
    UserEntity linked = findById(linkedUserId);
    UserEntity currentUser = findByUsername(currentUsername);

    FriendshipEntity friendshipEntity = new FriendshipEntity();
    friendshipEntity.setRequester(linked);
    friendshipEntity.setAddressee(currentUser);
    friendshipRepository.delete(friendshipEntity);
  }

  @Override
  @Transactional
  public void deleteFriendshipRequest(String linkedUserId, String currentUsername) {
    UserEntity linked = findById(linkedUserId);
    UserEntity currentUser = findByUsername(currentUsername);

    FriendshipEntity request = findFriendship(currentUser, linked);
    friendshipRepository.delete(request);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<FriendshipEntity> findFrienship(UserEntity requester, UserEntity addresse) {
    return friendshipRepository.findFriendship(requester, addresse);
  }

  @Override
  @Transactional
  public UserEntity updateUser(String username, String firstname, String surname, String avatar,
      String countryId) {
    UserEntity forUpdate = userRepository.findByUsername(username).orElseThrow();
    forUpdate.setFirstname(firstname);
    forUpdate.setSurname(surname);
    forUpdate.setAvatar(avatar);
    if (!isEmpty(countryId)) {
      forUpdate.setCountryId(UUID.fromString(countryId));
    }
    UserEntity updated = userRepository.save(forUpdate);
    return updated;
  }

  private boolean isEmpty(String s) {
    return s.equals(STRING_EMPTY);
  }

  private boolean isPageableIsNotExistsIn(Pageable pageable) {
    return pageable.getPageSize() == 0 && pageable.getPageNumber() == 0;
  }

  private UserEntity findById(String userId) {
    return userRepository.findById(UUID.fromString(userId))
        .orElseThrow(() -> new ResourceNotFoundException(
            String.format("UserId %s not found", userId)));
  }

  private FriendshipEntity findFriendship(UserEntity userFirst, UserEntity userSecond) {
    return friendshipRepository.findFriendship(userFirst, userSecond)
        .orElseThrow(() -> new ResourceNotFoundException(
            String.format("Friendship between %s and %s not found",
                userFirst.getUsername(),
                userSecond.getUsername())));
  }

}
