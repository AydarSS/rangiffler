package project.qa.rangiffler.model.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record Feed(
    @JsonProperty("username")
    String username,
    @JsonProperty("withFrinds")
    boolean withFrinds,
    @JsonProperty("photos")
    List<Photo> photos,
    @JsonProperty("stats")
    List<Stat> stats
) {

}
