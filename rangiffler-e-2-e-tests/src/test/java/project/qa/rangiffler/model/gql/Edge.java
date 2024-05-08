package project.qa.rangiffler.model.gql;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Edge<T> {
  @JsonProperty("node")
  T userNode;
  @JsonProperty("__typename")
  protected String typename;

}
