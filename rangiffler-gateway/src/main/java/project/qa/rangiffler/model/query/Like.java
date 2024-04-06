package project.qa.rangiffler.model.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.UUID;

public record Like(
    @JsonProperty("userId")
    UUID userId,
    @JsonProperty("username")
    String username,
    @JsonProperty("creationDate")
    Date creationDate
) {

}
