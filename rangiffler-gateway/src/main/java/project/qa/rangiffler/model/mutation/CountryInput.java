package project.qa.rangiffler.model.mutation;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CountryInput(
    @JsonProperty("code")
    String code
) {

}
