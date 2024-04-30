package project.qa.rangiffler.service.utils;

import project.qa.rangiffler.data.repository.UserRepository;

public class FindFriendsMethodResolver extends FindMethodResolver{

  private final UserRepository userRepository;


  public FindFriendsMethodResolver(boolean firstParameter, boolean secondParameter,
      UserRepository userRepository) {
    super(firstParameter, secondParameter);
    this.userRepository = userRepository;
  }

  @Override
  void callMethodWithNoAdditionalParameters() {

  }

  @Override
  void callMethodWithSearchQueryParameter() {

  }

  @Override
  void callMethodWithPageableParameter() {

  }

  @Override
  void callMethodWithAllParameters() {

  }
}
