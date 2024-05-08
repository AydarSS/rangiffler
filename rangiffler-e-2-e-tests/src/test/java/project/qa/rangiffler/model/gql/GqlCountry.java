package project.qa.rangiffler.model.gql;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GqlCountry extends GqlResponse<GqlCountry> {

  @JsonProperty("id")
  private UUID id;
  @JsonProperty("flag")
  private String flag;
  @JsonProperty("code")
  private String code;
  @JsonProperty("name")
  private String name;
}
