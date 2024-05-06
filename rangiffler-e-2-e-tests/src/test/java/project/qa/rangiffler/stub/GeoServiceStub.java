package project.qa.rangiffler.stub;

import guru.qa.grpc.rangiffler.GeoServiceGrpc;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.qameta.allure.grpc.AllureGrpc;
import project.qa.rangiffler.config.Config;
import project.qa.rangiffler.utils.GrpcConsoleInterceptor;

public class GeoServiceStub {

  private static final Config CFG = Config.getInstance();
  private static Channel channel;
  public static GeoServiceGrpc.GeoServiceBlockingStub stub;

  static {
    channel = ManagedChannelBuilder.forAddress(CFG.geoGrpcHost(), CFG.geoGrpcPort())
        .intercept(new AllureGrpc(), new GrpcConsoleInterceptor())
        .usePlaintext()
        .maxInboundMessageSize(999999999)
        .build();

    stub = GeoServiceGrpc.newBlockingStub(channel);
  }


}
