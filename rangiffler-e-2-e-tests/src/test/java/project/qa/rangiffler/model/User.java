package project.qa.rangiffler.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.UUID;

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
    Country country,
    TestData testData
) {

  public static User withOnlyUsernameAndPassword(String username, String password){
    return  new User(null,
        username,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        new TestData(password));
  }

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
        country,
        null);
  }

}
