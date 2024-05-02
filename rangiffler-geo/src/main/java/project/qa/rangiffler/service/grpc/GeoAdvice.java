package project.qa.rangiffler.service.grpc;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;
import project.qa.rangiffler.service.grpc.ex.ResourceNotFoundException;

@GrpcAdvice
public class GeoAdvice {

  @GrpcExceptionHandler(ResourceNotFoundException.class)
  public StatusRuntimeException handleResourceNotFoundException (ResourceNotFoundException ex) {
    return Status.INVALID_ARGUMENT.withDescription(ex.getMessage()).asRuntimeException();
  }

}
