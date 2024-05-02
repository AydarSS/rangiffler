package project.qa.rangiffler.service.api;

import java.util.List;
import java.util.Optional;
import project.qa.rangiffler.data.CountryEntity;

public interface GeoService {

  Optional<CountryEntity> findByCode(String code);

  Optional<CountryEntity> findById(String id);

  List<CountryEntity> findAll();
}
