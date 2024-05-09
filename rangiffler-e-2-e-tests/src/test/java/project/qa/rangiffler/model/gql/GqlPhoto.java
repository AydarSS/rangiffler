package project.qa.rangiffler.model.gql;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import project.qa.rangiffler.model.Country;
import project.qa.rangiffler.model.Likes;

@Getter
@Setter
public class GqlPhoto extends GqlResponse<GqlPhoto> {

  @JsonProperty("photo")
  protected GqlPhoto photo;

  @JsonProperty("id")
  protected UUID id;
  @JsonProperty("src")
  protected String src;
  @JsonProperty("country")
  protected GqlCountry contry;
  @JsonProperty("description")
  protected String description;
  @JsonProperty("creationDate")
  protected LocalDate creationDate;
  @JsonProperty("likes")
  protected GqlLikes likes;


}
