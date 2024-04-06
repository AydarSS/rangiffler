package project.qa.rangiffler.model.mutation;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserInput(
    @JsonProperty("firstname")
    String firstname,
    @JsonProperty("surname")
    String surname,
    @JsonProperty("avatar")
    String avatar,
    @JsonProperty("location")
    CountryInput location
) {

}
