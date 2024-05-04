package project.qa.rangiffler.service.api;

import java.util.List;
import java.util.UUID;
import project.qa.rangiffler.model.query.Country;

public interface GeoClient {

  Country findByCode(String code);

  Country findById(UUID id);

  List<Country> findAll();


}
