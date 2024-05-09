package project.qa.rangiffler.model.gql;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GqlDeletePhoto extends  GqlResponse<GqlDeletePhoto>{

  @JsonProperty("deletePhoto")
  protected boolean deletePhoto;
}
