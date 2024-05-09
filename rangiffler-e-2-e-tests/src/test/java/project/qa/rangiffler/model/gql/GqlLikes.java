package project.qa.rangiffler.model.gql;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import project.qa.rangiffler.model.Like;

public class GqlLikes {

  @JsonProperty("total")
  protected int total;
  @JsonProperty("likes")
  protected List<Like> likes;
  @JsonProperty("__typename")
  protected String typename;

}
