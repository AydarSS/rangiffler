package project.qa.rangiffler.config;

import java.util.List;

public interface Config {

  static Config getInstance() {
    return "docker".equals(System.getProperty("test.env"))
        ? DockerConfig.instance
        : LocalConfig.instance;
  }

  String frontUrl();

  String authUrl();

  String gatewayUrl();

  String jdbcHost();

  String geoUrl();

  String userUrl();

  String photoUrl();

  String kafkaAddress();

  String userGrpcHost();
  String geoGrpcHost();
  String photoGrpcHost();

  default int userGrpcPort() {return 8090;}
  default int geoGrpcPort() {return 8086;}
  default int photoGrpcPort() {return 8096;}

  default String jdbcUser() {
    return "root";
  }

  default String jdbcPassword() {
    return "secret";
  }

  default int jdbcPort() {
    return 3306;
  }

  default List<String> kafkaTopics() {
    return List.of("users");
  }

}
