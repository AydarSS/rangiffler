package project.qa.rangiffler.model.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import project.qa.rangiffler.model.mutation.CountryInput;

public record Country(
    @JsonProperty("id")
    String flag,
    @JsonProperty("code")
    String code,
    @JsonProperty("name")
    String name
) {

    public static Country fromCountryInput(CountryInput countryInput){
        return new Country(null,
            countryInput.code(),
            null);
    }

}
