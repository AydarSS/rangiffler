package project.qa.rangiffler.model.gql;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import project.qa.rangiffler.model.FriendStatus;

@Getter
@Setter
public class GqlUser extends GqlResponse<GqlUser> {

  private GqlUser user;
  private GqlUser updateUser;
  private GqlUser friendship;

  @JsonProperty("id")
  private UUID id;
  @JsonProperty("username")
  private String username;
  @JsonProperty("firstname")
  private String firstname;
  @JsonProperty("surname")
  private String surname;
  @JsonProperty("avatar")
  private String avatar;
  @JsonProperty("friendStatus")
  private FriendStatus friendState;
  @JsonProperty("friends")
  private PageableResponse<GqlUser> friends;
  @JsonProperty("incomeInvitations")
  private PageableResponse<GqlUser> incomeInvitations;
  @JsonProperty("outcomeInvitations")
  private PageableResponse<GqlUser> outcomeInvitations;
  @JsonProperty("location")
  private GqlCountry country;

}



