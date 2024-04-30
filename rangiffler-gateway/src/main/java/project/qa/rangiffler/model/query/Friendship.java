package project.qa.rangiffler.model.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;
import project.qa.rangiffler.model.mutation.FriendshipAction;
import project.qa.rangiffler.model.mutation.FriendshipInput;

public record Friendship(
    @JsonProperty("requester")
    String requesterUsername,
    @JsonProperty("addressee")
    UUID addressee,
    @JsonProperty("action")
    FriendshipAction action
) {

  public static Friendship fromFriendshipInput(FriendshipInput friendshipInput, String username) {
    return new Friendship(username,
        friendshipInput.user(),
        friendshipInput.action());
  }
}
