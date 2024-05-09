package project.qa.rangiffler.model.gql;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import project.qa.rangiffler.model.Country;

@Getter
@Setter
public class GqlStat {

  @JsonProperty("count")
  int count;
  @JsonProperty("country")
  GqlCountry country;
  @JsonProperty("__typename")
  protected String typename;

}
