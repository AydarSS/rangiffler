package project.qa.rangiffler.service;

import java.util.List;
import org.springframework.stereotype.Service;
import project.qa.rangiffler.model.query.Country;
import project.qa.rangiffler.service.api.GeoClient;
import project.qa.rangiffler.service.api.GrpcGeoClient;

@Service
public class GeoService {

  private final GeoClient geoClient;

  public GeoService() {
    this.geoClient = new GrpcGeoClient();
  }

  public List<Country> countries() {
    return geoClient.findAll();
  }
}
