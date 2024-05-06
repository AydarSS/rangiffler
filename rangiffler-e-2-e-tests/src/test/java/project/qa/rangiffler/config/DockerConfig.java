package project.qa.rangiffler.config;

import com.codeborne.selenide.Configuration;

public class DockerConfig implements Config{

  static final DockerConfig instance = new DockerConfig();

  private DockerConfig() {
  }

  static {
    Configuration.remote = "http://localhost:4444/wd/hub";
    Configuration.browser = "chrome";
  }

  @Override
  public String frontUrl() {
    return null;
  }

  @Override
  public String authUrl() {
    return null;
  }

  @Override
  public String gatewayUrl() {
    return null;
  }

  @Override
  public String jdbcHost() {
    return null;
  }

  @Override
  public String geoUrl() {
    return null;
  }

  @Override
  public String userUrl() {
    return null;
  }

  @Override
  public String photoUrl() {
    return null;
  }

  @Override
  public String kafkaAddress() {
    return null;
  }

  @Override
  public String userGrpcHost() {
    return null;
  }

  @Override
  public String geoGrpcHost() {
    return null;
  }

  @Override
  public String photoGrpcHost() {
    return null;
  }
}
