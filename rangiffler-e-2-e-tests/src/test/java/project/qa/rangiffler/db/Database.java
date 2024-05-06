package project.qa.rangiffler.db;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import project.qa.rangiffler.config.Config;

@RequiredArgsConstructor
public enum Database {

  AUTH("jdbc:mysql://%s:%d/rangiffler_auth"),
  GEO("jdbc:mysql://%s:%d/rangiffler_geo"),
  PHOTO("jdbc:mysql://%s:%d/rangiffler_photo"),
  USERDATA("jdbc:mysql://%s:%d/rangiffler_userdata");

  private final String url;

  private static final Config cfg = Config.getInstance();

  public String getUrl() {
    return String.format(
        url,
        cfg.jdbcHost(),
        cfg.jdbcPort()
    );
  }

}
