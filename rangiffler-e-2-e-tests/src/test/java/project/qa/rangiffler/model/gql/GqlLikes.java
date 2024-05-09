package project.qa.rangiffler.model.gql;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GqlLikes {

  @JsonProperty("total")
  protected int total;
  @JsonProperty("likes")
  protected List<GqlLike> likes;
  @JsonProperty("__typename")
  protected String typename;
}
