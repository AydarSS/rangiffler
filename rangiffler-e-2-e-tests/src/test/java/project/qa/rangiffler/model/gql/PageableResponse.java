package project.qa.rangiffler.model.gql;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageableResponse<T> {
  @JsonProperty("edges")
  protected List<Edge<T>> edges;
  @JsonProperty("pageInfo")
  GqlPageInfo pageInfo;
  @JsonProperty("__typename")
  protected String typename;


}
