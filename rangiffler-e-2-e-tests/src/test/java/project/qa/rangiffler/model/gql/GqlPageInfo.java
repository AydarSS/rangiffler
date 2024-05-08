package project.qa.rangiffler.model.gql;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GqlPageInfo {
  @JsonProperty("hasPreviousPage")
  protected boolean hasPreviousPage;
  @JsonProperty("hasNextPage")
  protected boolean hasNextPage;
  @JsonProperty("__typename")
  protected String pageInfo;

}
