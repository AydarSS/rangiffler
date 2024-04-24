package project.qa.rangiffler.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public record CountryJson(
    @JsonProperty("id")
    UUID id,
    @JsonProperty("code")
    String code,
    @JsonProperty("name")
    String name,
    @JsonProperty("flag")
    String flag
) {

}
