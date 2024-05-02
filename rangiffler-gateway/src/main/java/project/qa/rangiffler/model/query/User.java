package project.qa.rangiffler.model.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.UUID;
import project.qa.rangiffler.model.mutation.UserInput;

public record User(
    @JsonProperty("id")
    UUID id,
    @JsonProperty("username")
    String username,
    @JsonProperty("firstname")
    String firstname,
    @JsonProperty("surname")
    String surname,
    @JsonProperty("avatar")
    String avatar,
    @JsonProperty("friendStatus")
    FriendStatus friendStatus,
    @JsonProperty("friends")
    List<User> friends,
    @JsonProperty("incomeInvitations")
    List<User> incomeInvitations,
    @JsonProperty("outcomeInvitations")
    List<User> outcomeInvitations,
    @JsonProperty("country")
    Country country
) {

    public User withCountry(Country country) {
        return new User(this.id,
            this.username,
            this.firstname,
            this.surname,
            this.avatar,
            this.friendStatus,
            this.friends,
            this.incomeInvitations,
            this.outcomeInvitations,
            country);
    }

    public static User fromUserInput(UserInput userInput, String username) {
        return new User(null,
            username,
            userInput.firstname(),
            userInput.surname(),
            userInput.avatar(),
            null,
            null,
            null,
            null,
            Country.fromCountryInput(userInput.location()));
    }

}
