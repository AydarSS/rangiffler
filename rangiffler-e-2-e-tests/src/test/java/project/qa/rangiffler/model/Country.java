package project.qa.rangiffler.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

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

}
