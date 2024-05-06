package project.qa.rangiffler.stub;

import guru.qa.grpc.rangiffler.UserServiceGrpc;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.qameta.allure.grpc.AllureGrpc;
import project.qa.rangiffler.config.Config;
import project.qa.rangiffler.utils.GrpcConsoleInterceptor;

public class UserServiceStub {

  protected static final Config CFG = Config.getInstance();
  protected static Channel channel;
  public static UserServiceGrpc.UserServiceBlockingStub stub;

  static {
    channel = ManagedChannelBuilder.forAddress(CFG.userGrpcHost(), CFG.userGrpcPort())
        .intercept(new AllureGrpc(), new GrpcConsoleInterceptor())
        .usePlaintext()
        .maxInboundMessageSize(999999999)
        .build();

    stub = UserServiceGrpc.newBlockingStub(channel);
  }


}
