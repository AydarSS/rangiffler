package project.qa.rangiffler.service.api;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.qa.rangiffler.data.CountryEntity;
import project.qa.rangiffler.data.repository.CountryRepository;

@Service
public class GeoServiceImpl implements GeoService {

  @Autowired
  private CountryRepository countryRepository;

  public Optional<CountryEntity> findByCode(String code) {
    return countryRepository.findByCode(code);
  }

  public Optional<CountryEntity> findById(String id) {
    return countryRepository.findById(UUID.fromString(id));
  }

  public List<CountryEntity> findAll() {
    return countryRepository.findAll();
  }
}
