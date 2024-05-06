package project.qa.rangiffler.service.grpc;

import com.google.protobuf.Empty;
import guru.qa.grpc.rangiffler.CountryOuterClass.Country;
import guru.qa.grpc.rangiffler.CountryOuterClass.CountryByCodeRequest;
import guru.qa.grpc.rangiffler.CountryOuterClass.CountryByIdRequest;
import guru.qa.grpc.rangiffler.CountryOuterClass.CountryResponse;
import guru.qa.grpc.rangiffler.GeoServiceGrpc;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import java.util.List;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

@GrpcService
public class GrpcGeoReceiver extends GeoServiceGrpc.GeoServiceImplBase {

  private final GrpcGeoHandler handler;

  @Autowired
  public GrpcGeoReceiver(GrpcGeoHandler handler) {
    this.handler = handler;
  }

  @Override
  public void getAllCountries(Empty request, StreamObserver<CountryResponse> responseObserver) {
    List<Country> countryList = handler.getAllCountries();
    CountryResponse response = CountryResponse.newBuilder()
        .addAllCountry(countryList)
        .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void getCountryByCode(CountryByCodeRequest request,
      StreamObserver<Country> responseObserver) {
    try {
      Country response = handler.getByCode(request.getCode());
      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } catch (StatusRuntimeException e) {
      responseObserver.onError(e);
    }
  }

  @Override
  public void getCountryById(CountryByIdRequest request, StreamObserver<Country> responseObserver) {
    try {
      Country response = handler.getById(request.getId());
      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } catch (StatusRuntimeException e) {
      responseObserver.onError(e);
    }
  }
}
