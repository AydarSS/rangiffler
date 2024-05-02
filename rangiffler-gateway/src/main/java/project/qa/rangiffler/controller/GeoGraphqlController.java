package project.qa.rangiffler.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import project.qa.rangiffler.model.query.Country;
import project.qa.rangiffler.service.GeoService;

@Controller
public class GeoGraphqlController {

  private final GeoService geoService;

  @Autowired
  public GeoGraphqlController(GeoService geoService) {
    this.geoService = geoService;
  }

  @QueryMapping
  public List<Country> countries(@AuthenticationPrincipal Jwt principal) {
    return geoService.countries();
  }

}
