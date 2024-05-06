package project.qa.rangiffler.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
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
    LocalDate creationDate,
    @JsonProperty("likes")
    Likes likes

) {

}
