package project.qa.rangiffler.config;

import com.codeborne.selenide.Configuration;

public class LocalConfig implements Config {

  static final LocalConfig instance = new LocalConfig();

  static {
    Configuration.browserSize = "1980x1024";
  }

  private LocalConfig() {
  }

  @Override
  public String frontUrl() {
    return "http://127.0.0.1:3001";
  }

  @Override
  public String authUrl() {
    return "http://127.0.0.1:9000";
  }

  @Override
  public String gatewayUrl() {
    return "http://127.0.0.1:8080";
  }

  @Override
  public String jdbcHost() {
    return "localhost";
  }

  @Override
  public String geoUrl() {
    return "http://127.0.0.1:8085";
  }

  @Override
  public String userUrl() {
    return "http://127.0.0.1:8089";
  }

  @Override
  public String photoUrl() {
    return "http://127.0.0.1:8095";
  }

  @Override
  public String kafkaAddress() {
    return "localhost:9092";
  }

  @Override
  public String userGrpcHost() {
    return "localhost";
  }

  @Override
  public String geoGrpcHost() {
    return "localhost";
  }

  @Override
  public String photoGrpcHost() {
    return "localhost";
  }
}
