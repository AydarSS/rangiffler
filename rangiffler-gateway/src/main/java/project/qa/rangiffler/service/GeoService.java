package project.qa.rangiffler.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.qa.rangiffler.model.query.Country;
import project.qa.rangiffler.service.api.GeoClient;
import project.qa.rangiffler.service.api.GrpcGeoClient;

@Service
public class GeoService {

  private GeoClient geoClient;

  @Autowired
  public void setGeoClient(GeoClient geoClient) {
    this.geoClient = geoClient;
  }


  public List<Country> countries() {
    return geoClient.findAll();
  }
}
