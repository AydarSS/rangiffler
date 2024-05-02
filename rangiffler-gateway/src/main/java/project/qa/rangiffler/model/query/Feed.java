package project.qa.rangiffler.model.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.springframework.data.domain.Slice;

public record Feed(
    @JsonProperty("username")
    String username,
    @JsonProperty("withFriends")
    boolean withFriends,
    @JsonProperty("photos")
    List<Photo> photos,
    @JsonProperty("stats")
    List<Stat> stats
) {

}
