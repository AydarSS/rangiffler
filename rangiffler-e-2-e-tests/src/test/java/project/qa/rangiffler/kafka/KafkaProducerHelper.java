package project.qa.rangiffler.kafka;

import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.transaction.annotation.Transactional;
import project.qa.rangiffler.config.Config;
import project.qa.rangiffler.model.UserJson;

public class KafkaProducerHelper {

  private static final Config CFG = Config.getInstance();
  private static final Properties STR_KAFKA_PROPERTIES = new Properties();
  private final Producer<String, UserJson> producer;

  static {
    STR_KAFKA_PROPERTIES.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, CFG.kafkaAddress());
    STR_KAFKA_PROPERTIES.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
        StringSerializer.class.getName());
    STR_KAFKA_PROPERTIES.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
        JsonSerializer.class.getName());
  }

  public KafkaProducerHelper() {
    this.producer = new KafkaProducer<>(STR_KAFKA_PROPERTIES);
  }

  @Transactional
  public void sendMessage(UserJson user) {
    ProducerRecord<String, UserJson> producerRecord = new ProducerRecord<>("users", user);
    producer.send(producerRecord);
  }
}
