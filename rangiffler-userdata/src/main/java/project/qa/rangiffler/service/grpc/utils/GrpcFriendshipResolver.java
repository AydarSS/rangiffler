package project.qa.rangiffler.service.grpc.utils;

import guru.qa.grpc.rangiffler.UserOuterClass.FriendsStatus;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import project.qa.rangiffler.data.FriendshipEntity;
import project.qa.rangiffler.data.UserEntity;
import project.qa.rangiffler.service.api.UserDataServiceImpl;

@Component
public class GrpcFriendshipResolver {

  @Autowired
  private UserDataServiceImpl service;

  public FriendsStatus resolveFriendShipStatus(UserEntity current, UserEntity linked) {
    Optional<FriendshipEntity> maybeFriendOrInviteSent = service.findFrienship(current, linked);
    if (maybeFriendOrInviteSent.isPresent()) {
      switch (maybeFriendOrInviteSent.get().getStatus()) {
        case PENDING -> {
          return FriendsStatus.INVITATION_SENT;
        }
        case ACCEPTED -> {
          return FriendsStatus.FRIEND;
        }
      }
    }
    Optional<FriendshipEntity> maybeInviteReceived = service.findFrienship(linked, current);
    if (maybeInviteReceived.isPresent()) {
      return FriendsStatus.INVITATION_RECEIVED;
    }
    return FriendsStatus.NOT_FRIEND;
  }

}

