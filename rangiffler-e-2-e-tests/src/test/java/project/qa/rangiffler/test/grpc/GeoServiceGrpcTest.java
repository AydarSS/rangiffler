package project.qa.rangiffler.test.grpc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.google.protobuf.Empty;
import guru.qa.grpc.rangiffler.CountryOuterClass.Country;
import guru.qa.grpc.rangiffler.CountryOuterClass.CountryByCodeRequest;
import guru.qa.grpc.rangiffler.CountryOuterClass.CountryByIdRequest;
import guru.qa.grpc.rangiffler.CountryOuterClass.CountryResponse;
import io.qameta.allure.Step;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import project.qa.rangiffler.stub.GeoServiceStub;

public class GeoServiceGrpcTest {

  @DisplayName("Получаем все страны")
  @Test
  void findAllCountriesTest() {
    CountryResponse response = GeoServiceStub.stub.getAllCountries(Empty.newBuilder().build());
    List<Country> countries = response.getCountryList();

    assertThat(countries)
        .extracting(
            Country::getName,
            Country::getCode)
        .contains(
            tuple("Cyprus", "cy"),
            tuple("Russian Federation", "ru"));
  }

  @DisplayName("Получаем страну по коду ru")
  @Test
  void findByCodeTest() {
    Country response = getCountryByCode("ru");

    assertAll("Проверяем полученную страну",
        () -> assertEquals("Russian Federation", response.getName(), "Проверяем name"),
        () -> assertEquals("ru", response.getCode(), "Провеяем code"),
        () -> assertNotNull(response.getFlag(), "Flag не равен null"));
  }

  @DisplayName("Получаем страну по id")
  @Test
  void findByIdTest() {
    Country responseByCode = getCountryByCode("ru");

    CountryByIdRequest request =
        CountryByIdRequest.newBuilder()
            .setId(responseByCode.getId())
            .build();

    Country responseById = GeoServiceStub.stub.getCountryById(request);

    assertAll("Проверяем полученную страну",
        () -> assertEquals("Russian Federation", responseById.getName(), "Проверяем name"),
        () -> assertEquals("ru", responseById.getCode(), "Провеяем code"),
        () -> assertNotNull(responseById.getFlag(), "Flag не равен null"));
  }

  @Step("Получим страну по коду")
  private Country getCountryByCode(String code){
    CountryByCodeRequest request = CountryByCodeRequest.newBuilder()
        .setCode(code)
        .build();

    return GeoServiceStub.stub.getCountryByCode(request);
  }

}
