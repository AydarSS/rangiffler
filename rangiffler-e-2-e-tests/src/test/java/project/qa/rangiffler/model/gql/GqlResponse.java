package project.qa.rangiffler.model.gql;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class GqlResponse<T extends GqlResponse> {
  protected T data;
  @JsonProperty("__typename")
  protected String typename;
}
