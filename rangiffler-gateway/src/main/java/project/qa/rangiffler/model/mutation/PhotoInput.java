package project.qa.rangiffler.model.mutation;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public record PhotoInput(
    @JsonProperty("id")
    UUID id,
    @JsonProperty("src")
    String src,
    @JsonProperty("country")
    CountryInput country,
    @JsonProperty("description")
    String description,
    @JsonProperty("like")
    LikeInput like
) {

}
