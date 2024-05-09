package project.qa.rangiffler.model.gql;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GqlLike {

  @JsonProperty("user")
  protected UUID user;
  @JsonProperty("username")
  protected String username;
  @JsonProperty("creationDate")
  protected LocalDate creationDate;
}
