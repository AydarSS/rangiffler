package project.qa.rangiffler.model.query;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Stat(
    @JsonProperty("count")
    int count,
    @JsonProperty("country")
    Country country
) {

}
