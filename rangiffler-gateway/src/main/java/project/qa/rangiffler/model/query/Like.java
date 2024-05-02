package project.qa.rangiffler.model.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

public record Like(
    @JsonProperty("user")
    UUID user,
    @JsonProperty("username")
    String username,
    @JsonProperty("creationDate")
    LocalDate creationDate
) {

}
