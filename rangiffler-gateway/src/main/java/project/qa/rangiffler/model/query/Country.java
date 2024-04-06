package project.qa.rangiffler.model.query;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Country(
    @JsonProperty("id")
    String flag,
    @JsonProperty("code")
    String code,
    @JsonProperty("name")
    String name
) {

}
