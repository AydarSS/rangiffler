package project.qa.rangiffler.model.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.UUID;

public record Photo(
    @JsonProperty("id")
    UUID id,
    @JsonProperty("src")
    String src,
    @JsonProperty("contry")
    Country contry,
    @JsonProperty("description")
    String description,
    @JsonProperty("creationDate")
    Date creationDate,
    @JsonProperty("likes")
    Likes likes

) {

}
