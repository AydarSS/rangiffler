package project.qa.rangiffler.db;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.sql.DataSource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import project.qa.rangiffler.config.Config;

public enum DataSourceProvider {
  INSTANCE;

  private static final Config cfg = Config.getInstance();

  private final Map<Database, DataSource> store = new ConcurrentHashMap<>();

  public DataSource dataSource(Database database) {
    return store.computeIfAbsent(database, k -> {
      DriverManagerDataSource dataSource = new DriverManagerDataSource();
      dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
      dataSource.setUrl(k.getUrl());
      dataSource.setUsername(cfg.jdbcUser());
      dataSource.setPassword(cfg.jdbcPassword());
      return dataSource;
    });
  }
}
