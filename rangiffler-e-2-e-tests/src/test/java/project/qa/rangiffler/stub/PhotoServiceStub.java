package project.qa.rangiffler.stub;

import guru.qa.grpc.rangiffler.PhotoServiceGrpc;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.qameta.allure.grpc.AllureGrpc;
import project.qa.rangiffler.config.Config;
import project.qa.rangiffler.utils.GrpcConsoleInterceptor;

public class PhotoServiceStub {

  private static final Config CFG = Config.getInstance();
  private static Channel channel;
  public static PhotoServiceGrpc.PhotoServiceBlockingStub stub;

  static {
    channel = ManagedChannelBuilder.forAddress(CFG.photoGrpcHost(), CFG.photoGrpcPort())
        .intercept(new AllureGrpc(), new GrpcConsoleInterceptor())
        .usePlaintext()
        .maxInboundMessageSize(999999999)
        .build();

    stub = PhotoServiceGrpc.newBlockingStub(channel);
  }


}
