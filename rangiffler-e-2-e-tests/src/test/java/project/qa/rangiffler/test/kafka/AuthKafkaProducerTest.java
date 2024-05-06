package project.qa.rangiffler.test.kafka;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import project.qa.rangiffler.api.AuthApiClient;
import project.qa.rangiffler.extension.KafkaExtension;
import project.qa.rangiffler.kafka.KafkaService;
import project.qa.rangiffler.model.User;

@ExtendWith(KafkaExtension.class)
public class AuthKafkaProducerTest  {

  private final AuthApiClient authApiClient = new AuthApiClient();

  @DisplayName("Проверим отправку сообщения в Kafka")
  @Test
  void messageShouldBeProducedToKafkaAfterSuccessfulRegistrationTest() throws Exception {
    String username = new Faker().name().username();
    String password = "12345";
    authApiClient.doRegister(username, password);

    User userFromKafka = KafkaService.getMessage(username);

    Assertions.assertEquals(
        username,
        userFromKafka.username()
    );
  }
}
