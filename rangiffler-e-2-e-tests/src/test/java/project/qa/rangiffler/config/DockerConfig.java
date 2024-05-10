package project.qa.rangiffler.config;

import com.codeborne.selenide.Configuration;
import org.openqa.selenium.chrome.ChromeOptions;

public class DockerConfig implements Config{

  static final DockerConfig instance = new DockerConfig();

  private DockerConfig() {
  }

  static {
    Configuration.remote = "http://selenoid:4444/wd/hub";
    Configuration.browser = "chrome";
    Configuration.browserVersion = "117.0";
    Configuration.browserCapabilities = new ChromeOptions().addArguments("--no-sandbox");
    Configuration.browserSize = "1980x1024";
  }

  @Override
  public String frontUrl() {
    return "http://frontend.rangiffler.dc";
  }

  @Override
  public String authUrl() {
    return "http://auth.rangiffler.dc:9000";
  }

  @Override
  public String gatewayUrl() {
    return "http://gateway.rangiffler.dc:8080";
  }

  @Override
  public String jdbcHost() {
    return "rangiffler-all-db";
  }

  @Override
  public String geoUrl() {return "http://geo.rangiffler.dc:8085";
  }

  @Override
  public String userUrl() {
    return "http://userdata.rangiffler.dc:8089";
  }

  @Override
  public String photoUrl() {
    return "http://photo.rangiffler.dc:8095";
  }

  @Override
  public String kafkaAddress() {
    return "kafka:9092";
  }

  @Override
  public String userGrpcHost() {
    return "userdata.rangiffler.dc";
  }

  @Override
  public String geoGrpcHost() {
    return "geo.rangiffler.dc";
  }

  @Override
  public String photoGrpcHost() {
    return "photo.rangiffler.dc";
  }
}
