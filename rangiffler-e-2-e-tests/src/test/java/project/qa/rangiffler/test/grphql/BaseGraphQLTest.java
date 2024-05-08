package project.qa.rangiffler.test.grphql;

import io.qameta.allure.junit5.AllureJunit5;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import project.qa.rangiffler.api.GatewayGqlApiClient;
import project.qa.rangiffler.config.Config;
import project.qa.rangiffler.extension.AddPeopleInMethodResolver;
import project.qa.rangiffler.extension.ApiLoginExtension;
import project.qa.rangiffler.extension.ContextHolderExtension;
import project.qa.rangiffler.extension.GrpcAddUsersExtension;


@ExtendWith({ContextHolderExtension.class, AllureJunit5.class, GrpcAddUsersExtension.class,
    AddPeopleInMethodResolver.class})
public abstract class BaseGraphQLTest {

  @RegisterExtension
  protected final ApiLoginExtension apiLoginExtension = new ApiLoginExtension(false);

  protected static final Config CFG = Config.getInstance();

  protected final GatewayGqlApiClient gatewayGqlApiClient = new GatewayGqlApiClient();

}
