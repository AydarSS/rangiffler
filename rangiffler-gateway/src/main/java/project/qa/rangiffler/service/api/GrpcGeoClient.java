package project.qa.rangiffler.service.api;

import com.google.protobuf.Empty;
import guru.qa.grpc.rangiffler.CountryOuterClass;
import guru.qa.grpc.rangiffler.CountryOuterClass.CountryByCodeRequest;
import guru.qa.grpc.rangiffler.CountryOuterClass.CountryByIdRequest;
import guru.qa.grpc.rangiffler.CountryOuterClass.CountryResponse;
import guru.qa.grpc.rangiffler.GeoServiceGrpc;
import guru.qa.grpc.rangiffler.GeoServiceGrpc.GeoServiceBlockingStub;
import io.grpc.StatusRuntimeException;
import java.util.List;
import java.util.UUID;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import project.qa.rangiffler.model.query.Country;

@Component
public class GrpcGeoClient implements GeoClient {

  private static final Logger LOG = LoggerFactory.getLogger(GrpcGeoClient.class);
  private GeoServiceGrpc.GeoServiceBlockingStub geoServiceBlockingStub;

  @GrpcClient("grpcGeoClient")
  public void setGeoServiceBlockingStub(
      GeoServiceBlockingStub geoServiceBlockingStub) {
    this.geoServiceBlockingStub = geoServiceBlockingStub;
  }

  @Override
  public Country findByCode(String code) {
    CountryByCodeRequest request =
        CountryByCodeRequest.newBuilder()
            .setCode(code)
            .build();
    try {
      CountryOuterClass.Country response = geoServiceBlockingStub.getCountryByCode(request);
      return fromGrpc(response);
    } catch (StatusRuntimeException ex) {
      throw new RuntimeException(ex);
    }
  }

  @Override
  public Country findById(UUID id) {
    CountryByIdRequest request =
        CountryByIdRequest.newBuilder()
            .setId(id.toString())
            .build();
    try {
      CountryOuterClass.Country response = geoServiceBlockingStub.getCountryById(request);
      return fromGrpc(response);
    } catch (StatusRuntimeException ex) {
      throw new RuntimeException(ex);
    }
  }

  @Override
  public List<Country> findAll() {
    CountryResponse response = geoServiceBlockingStub.getAllCountries(Empty.newBuilder().build());
    return response.getCountryList().stream().map(this::fromGrpc).toList();
  }


  private Country fromGrpc(CountryOuterClass.Country grpcCountry) {
    return new Country(UUID.fromString(grpcCountry.getId()),
        grpcCountry.getFlag(),
        grpcCountry.getCode(),
        grpcCountry.getName());
  }
}