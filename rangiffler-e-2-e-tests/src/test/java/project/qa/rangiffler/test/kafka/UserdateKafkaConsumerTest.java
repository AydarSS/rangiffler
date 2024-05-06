package project.qa.rangiffler.test.kafka;

import static org.awaitility.Awaitility.await;

import com.github.javafaker.Faker;
import java.time.Duration;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import project.qa.rangiffler.db.DataSourceProvider;
import project.qa.rangiffler.db.Database;
import project.qa.rangiffler.kafka.KafkaProducerHelper;
import project.qa.rangiffler.model.UserJson;

public class UserdateKafkaConsumerTest {

  private final NamedParameterJdbcOperations userJdbc = new NamedParameterJdbcTemplate(
      DataSourceProvider.INSTANCE.dataSource(Database.USERDATA));
  private final KafkaProducerHelper kafkaProducerHelper = new KafkaProducerHelper();

  @DisplayName("Проверим получение сообщения из Kafka")
  @Test
  void messageShouldBeSavedInDbTest() {
    String username = new Faker().name().username();
    System.out.println(username);
    UserJson userJson = new UserJson(username);
    kafkaProducerHelper.sendMessage(userJson);

    String sql = String.format("""
        SELECT count(*) as count
        FROM user
        WHERE username = '%s'
        """, username);

    await()
        .atMost(30, TimeUnit.SECONDS)
        .pollInterval(Duration.ofSeconds(1))
        .until(() ->
            (long) userJdbc.queryForList(sql, new HashMap<>()).get(0).get("count") == 1L
        );
  }
}
