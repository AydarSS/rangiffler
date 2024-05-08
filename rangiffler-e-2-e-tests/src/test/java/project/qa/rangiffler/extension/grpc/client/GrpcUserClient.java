package project.qa.rangiffler.extension.grpc.client;

import guru.qa.grpc.rangiffler.UserOuterClass;
import guru.qa.grpc.rangiffler.UserOuterClass.UserAbout;
import guru.qa.grpc.rangiffler.UserOuterClass.UserByUsernameRequest;
import guru.qa.grpc.rangiffler.UserOuterClass.UserByUsernameResponse;
import io.grpc.StatusRuntimeException;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import project.qa.rangiffler.extension.grpc.utils.TypeConverter;
import project.qa.rangiffler.model.User;
import project.qa.rangiffler.stub.UserServiceStub;

public class GrpcUserClient {

  private final String STRING_EMPTY = "";
  private final String CALLING_GRPC_ERROR_TEXT = "### Error while calling gRPC server to UserData service";
  private static final Logger LOG = LoggerFactory.getLogger(GrpcUserClient.class);
  private final TypeConverter typeConverter = new TypeConverter();

  public User byUsername(String username) {
    try {
      UserByUsernameResponse response = UserServiceStub.stub.getUserByUsername(
          UserByUsernameRequest.newBuilder()
              .setUsername(username)
              .build());
      return typeConverter.fromProtoToUser(response.getUser());
    } catch (Exception ex) {
      LOG.error(CALLING_GRPC_ERROR_TEXT, ex);
      throw new RuntimeException(ex);
    }
  }

  public User updateUser(User user) {
    UserOuterClass.User userGrpc;
    try {
      userGrpc = UserServiceStub.stub.updateUser(
          UserAbout.newBuilder()
              .setUsername(user.username())
              .setFirstname(user.firstname())
              .setSurname(user.surname())
              .setAvatar(user.avatar())
              .setCountryId(
                  Objects.isNull(user.country().id()) ? STRING_EMPTY
                      : user.country().id().toString())
              .build()
      );
    } catch (StatusRuntimeException e) {
      LOG.error(CALLING_GRPC_ERROR_TEXT, e);
      throw new RuntimeException(e);
    }
    return typeConverter.fromProtoToUser(userGrpc);
  }

}
