package project.qa.rangiffler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import project.qa.rangiffler.service.PropertiesLogger;

@SpringBootApplication
public class RangifflerPhotoApplication {

  public static void main(String[] args) {
    SpringApplication springApplication = new SpringApplication(
        RangifflerPhotoApplication.class);
    springApplication.addListeners(new PropertiesLogger());
    springApplication.run(args);
  }

}
