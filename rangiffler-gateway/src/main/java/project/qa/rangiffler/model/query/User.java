package project.qa.rangiffler.model.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.UUID;

public record User(
    @JsonProperty("id")
    UUID id,
    @JsonProperty("username")
    String username,
    @JsonProperty("surname")
    String surname,
    @JsonProperty("avatar")
    String avatar,
    @JsonProperty("friends")
    List<User> friends,
    @JsonProperty("incomeInvitations")
    List<User> incomeInvitations,
    @JsonProperty("outcomeInvitations")
    List<User> outcomeInvitations,
    @JsonProperty("country")
    Country country
) {

}
