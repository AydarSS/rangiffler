package project.qa.rangiffler.model.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record Likes(
    @JsonProperty("total")
    int total,
    @JsonProperty("likes")
    List<Like> likes
) {

}
