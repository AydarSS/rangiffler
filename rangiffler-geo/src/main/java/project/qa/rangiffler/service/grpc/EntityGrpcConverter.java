package project.qa.rangiffler.service.grpc;

import guru.qa.grpc.rangiffler.CountryOuterClass.Country;
import project.qa.rangiffler.data.CountryEntity;

public class EntityGrpcConverter {

  public static Country fromCountryEntity(CountryEntity country){
    return Country.newBuilder()
        .setId(country.getId().toString())
        .setCode(country.getCode())
        .setName(country.getName())
        .setFlag(country.getFlag())
        .build();
  }

}
