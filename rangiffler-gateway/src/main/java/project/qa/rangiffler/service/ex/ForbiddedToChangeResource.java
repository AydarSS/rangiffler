package project.qa.rangiffler.service.ex;

public class ForbiddedToChangeResource extends RuntimeException{

  public ForbiddedToChangeResource(String message) {
    super(message);
  }
}
