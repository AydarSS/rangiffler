package project.qa.rangiffler.model.gql;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GqlFeed extends GqlResponse<GqlFeed> {

  private GqlFeed feed;

  @JsonProperty("username")
  String username;
  @JsonProperty("withFriends")
  boolean withFriends;
  @JsonProperty("photos")
  PageableResponse<GqlPhoto> photos;
  @JsonProperty("stat")
  List<GqlStat> stat;

}
