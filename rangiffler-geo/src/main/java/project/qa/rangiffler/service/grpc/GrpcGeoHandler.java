package project.qa.rangiffler.service.grpc;

import guru.qa.grpc.rangiffler.CountryOuterClass.Country;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import project.qa.rangiffler.data.CountryEntity;
import project.qa.rangiffler.service.api.GeoService;
import project.qa.rangiffler.service.grpc.ex.ResourceNotFoundException;

@Component
public class GrpcGeoHandler {

  @Autowired
  private GeoService geoService;

  public List<Country> getAllCountries() {
    return geoService.findAll()
        .stream()
        .map(EntityGrpcConverter::fromCountryEntity)
        .toList();
  }

  public Country getByCode(String code) {
    CountryEntity entity = geoService.findByCode(code)
        .orElseThrow(() -> new ResourceNotFoundException(
            String.format("Country by code %s not found", code)));
    return EntityGrpcConverter.fromCountryEntity(entity);
  }

  public Country getById(String id) {
    CountryEntity entity = geoService.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(
            String.format("Country by id %s not found", id)));
    return EntityGrpcConverter.fromCountryEntity(entity);
  }

}
