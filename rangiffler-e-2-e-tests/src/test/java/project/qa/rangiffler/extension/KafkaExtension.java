package project.qa.rangiffler.extension;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.extension.ExtensionContext;
import project.qa.rangiffler.kafka.KafkaService;

public class KafkaExtension implements SuiteExtension {

  private static final KafkaService ks = new KafkaService();
  private static final ExecutorService executor = Executors.newSingleThreadExecutor();


  @Override
  public void beforeSuite(ExtensionContext extensionContext) {
    executor.execute(ks);
    executor.shutdown();
  }

  @Override
  public void afterSuite() {
    ks.stop();
  }
}
