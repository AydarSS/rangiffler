package project.qa.rangiffler.extension.grpc.client;

import com.google.protobuf.Empty;
import guru.qa.grpc.rangiffler.CountryOuterClass;
import guru.qa.grpc.rangiffler.CountryOuterClass.CountryByCodeRequest;
import guru.qa.grpc.rangiffler.CountryOuterClass.CountryByIdRequest;
import guru.qa.grpc.rangiffler.CountryOuterClass.CountryResponse;
import java.util.List;
import java.util.UUID;
import project.qa.rangiffler.extension.grpc.utils.TypeConverter;
import project.qa.rangiffler.model.Country;
import project.qa.rangiffler.stub.GeoServiceStub;

public class GrpcGeoClient {

  private final TypeConverter typeConverter = new TypeConverter();


  public Country findByCode(String code) {
    CountryByCodeRequest request =
        CountryByCodeRequest.newBuilder()
            .setCode(code)
            .build();
    CountryOuterClass.Country response = GeoServiceStub.stub.getCountryByCode(request);
    return typeConverter.fromGrpc(response);
  }

  public Country findById(UUID id) {
    CountryByIdRequest request =
        CountryByIdRequest.newBuilder()
            .setId(id.toString())
            .build();
    CountryOuterClass.Country response = GeoServiceStub.stub.getCountryById(request);
    return typeConverter.fromGrpc(response);
  }

  public List<Country> findAll() {
    CountryResponse response = GeoServiceStub.stub.getAllCountries(Empty.newBuilder().build());
    return response.getCountryList().stream().map(country -> typeConverter.fromGrpc(country))
        .toList();
  }


}
