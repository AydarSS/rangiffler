package project.qa.rangiffler.service.api;

@FunctionalInterface
public interface UsersRequest<T> {

  T send();

}
