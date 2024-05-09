package project.qa.rangiffler.model.gql;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GqlError extends GqlErrorResponse<GqlError> {

  @JsonProperty("message")
  protected String message;
  @JsonProperty("locations")
  protected List<Map<String, Integer>> locations;
  @JsonProperty("path")
  protected List<String> path;
  @JsonProperty("extensions")
  protected Map<String, String> extensions;
  @JsonProperty("data")
  protected Map<String, String> data;
}


