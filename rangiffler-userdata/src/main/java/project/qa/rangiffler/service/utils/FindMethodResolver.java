package project.qa.rangiffler.service.utils;

import guru.qa.grpc.rangiffler.UserOuterClass.LinkedUsersByUsernameRequest;
import project.qa.rangiffler.data.UserEntity;

public abstract class FindMethodResolver {

  private final boolean isSearchQueryExists;
  private final boolean isPageableExists;

  public FindMethodResolver(boolean firstParameter, boolean secondParameter) {
    this.isSearchQueryExists = firstParameter;
    this.isPageableExists = secondParameter;
  }

  public void callFindMethod() {
    if (isSearchQueryExists && isPageableExists) {
      callMethodWithNoAdditionalParameters();
    }
    if (isSearchQueryExists) {
      callMethodWithSearchQueryParameter();
    }
    if (isPageableExists) {
      callMethodWithPageableParameter();
    } else {
      callMethodWithAllParameters();
    }
  }

  abstract void callMethodWithNoAdditionalParameters();

  abstract void callMethodWithSearchQueryParameter();

  abstract void callMethodWithPageableParameter();

  abstract void callMethodWithAllParameters();

}
