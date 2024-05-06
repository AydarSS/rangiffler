package project.qa.rangiffler.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.Nonnull;
import org.apache.kafka.clients.consumer.CommitFailedException;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import project.qa.rangiffler.config.Config;
import project.qa.rangiffler.model.User;

public class KafkaService implements Runnable {

  private static final Logger LOG = LoggerFactory.getLogger(KafkaService.class);
  private static final Config CFG = Config.getInstance();
  private static final WaitForOne<String, User> STORE = new WaitForOne<>();
  private static final ObjectMapper OM = new ObjectMapper();

  private final AtomicBoolean threadStarted = new AtomicBoolean(true);
  private final Consumer<String, String> stringConsumer;

  private static final Properties STR_KAFKA_PROPERTIES = new Properties();

  static {
    STR_KAFKA_PROPERTIES.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, CFG.kafkaAddress());
    STR_KAFKA_PROPERTIES.put(ConsumerConfig.GROUP_ID_CONFIG, "stringKafkaStringConsumerService");
    STR_KAFKA_PROPERTIES.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
        StringDeserializer.class.getName());
    STR_KAFKA_PROPERTIES.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
        StringDeserializer.class.getName());
    STR_KAFKA_PROPERTIES.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
  }

  public KafkaService() {
    this.stringConsumer = new KafkaConsumer<>(STR_KAFKA_PROPERTIES);
    this.stringConsumer.subscribe(CFG.kafkaTopics());
  }

  public void stop() {
    this.threadStarted.set(false);
  }

  public static User getMessage(String username) throws InterruptedException {
    return STORE.wait(username);
  }

  @Override
  public void run() {
    try {
      while (threadStarted.get()) {
        LOG.info("### TRY TO POLL");
        ConsumerRecords<String, String> strRecords = stringConsumer.poll(Duration.ofMillis(500));
        for (ConsumerRecord<String, String> record : strRecords) {
          logRecord(record);
          deserializeRecord(record.value());
        }
        try {
          stringConsumer.commitSync();
        } catch (CommitFailedException e) {
          LOG.warn("### Commit failed: " + e.getMessage());
        }
      }
    } catch (Exception e) {
      LOG.error("Error while consuming", e);
    } finally {
      stringConsumer.close();
      Thread.currentThread().interrupt();
    }
  }

  private void deserializeRecord(@Nonnull String recordValue) {
    try {
      User user = OM.readValue(recordValue, User.class);

      if (user == null || user.username() == null) {
        LOG.info("### Empty username in message ###");
        return;
      }

      STORE.provide(user.username(), user);
    } catch (JsonProcessingException e) {
      LOG.warn("### Parse message fail: " + e.getMessage());
    }
  }

  private void logRecord(@Nonnull ConsumerRecord<String, String> record) {
    LOG.info(
        String.format("topic = %s, \npartition = %d, \noffset = %d, \nkey = %s, \nvalue = %s\n\n",
            record.topic(),
            record.partition(),
            record.offset(),
            record.key(),
            record.value()));
  }
}
