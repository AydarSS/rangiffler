package project.qa.rangiffler.ex;

public class ForbiddedToChangeResource extends RuntimeException{

  public ForbiddedToChangeResource(String message) {
    super(message);
  }
}
