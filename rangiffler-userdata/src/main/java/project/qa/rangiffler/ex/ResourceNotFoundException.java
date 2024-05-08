package project.qa.rangiffler.ex;

public class ResourceNotFoundException extends RuntimeException  {

  public ResourceNotFoundException() {
    super("Resource(entity) not found");
  }

  public ResourceNotFoundException(String message) {
    super(message);
  }
}
