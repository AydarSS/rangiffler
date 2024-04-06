package project.qa.rangiffler.model.mutation;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public record FriendshipInput(
    @JsonProperty("user")
    UUID user,
    @JsonProperty("action")
    FriendshipAction action
) {

}
