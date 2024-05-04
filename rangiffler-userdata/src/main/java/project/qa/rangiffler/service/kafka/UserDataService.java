package project.qa.rangiffler.service.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import project.qa.rangiffler.data.UserEntity;
import project.qa.rangiffler.data.repository.UserRepository;
import project.qa.rangiffler.model.UserJson;

@Component
public class UserDataService {

  private static final Logger LOG = LoggerFactory.getLogger(UserDataService.class);

  private final UserRepository userRepository;

  @Autowired
  public UserDataService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Transactional
  @KafkaListener(topics = "users", groupId = "userdata")
  public void listener(@Payload UserJson user, ConsumerRecord<String, UserJson> cr) {
    LOG.info("### Kafka topic [users] received message: " + user.username());
    LOG.info("### Kafka consumer record: " + cr.toString());
    UserEntity userDataEntity = new UserEntity();
    userDataEntity.setUsername(user.username());
    UserEntity userEntity = userRepository.save(userDataEntity);
    LOG.info(String.format(
        "### User '%s' successfully saved to database with id: %s",
        user.username(),
        userEntity.getId()
    ));
  }

}
