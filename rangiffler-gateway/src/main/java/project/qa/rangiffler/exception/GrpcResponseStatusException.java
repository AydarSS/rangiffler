package project.qa.rangiffler.exception;

import io.grpc.Status;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class GrpcResponseStatusException extends ResponseStatusException {

  public GrpcResponseStatusException(HttpStatusCode httpStatusCode, Status status) {
    super(httpStatusCode, status.getDescription());
  }
}
