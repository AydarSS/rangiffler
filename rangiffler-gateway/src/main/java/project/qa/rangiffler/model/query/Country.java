package project.qa.rangiffler.model.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;
import project.qa.rangiffler.model.mutation.CountryInput;

public record Country(
    @JsonProperty("id")
    UUID id,
    @JsonProperty("flag")
    String flag,
    @JsonProperty("code")
    String code,
    @JsonProperty("name")
    String name
) {

    public static Country withOnlyId(String id) {
        return new Country(UUID.fromString(id),
            null,
            null,
            null);
    }

    public static Country withOnlyCode(String code) {
        return new Country(null,
            null,
            code,
            null);
    }

    public static Country fromCountryInput(CountryInput countryInput){
        return new Country(null,
            null,
            countryInput.code(),
            null);
    }

    public static Country emptyContry(){
        return new Country(null,"","","");
    }

}
