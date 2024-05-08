package project.qa.rangiffler.page.message;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ErrorMsg implements Msg {
  SHORT_PASSWORD("Allowed password length should be from 3 to 12 characters"),
  CAN_NOT_UPDATE_POST("Can not update post"),
  CAN_NOT_DELETE_POST("Can not delete post"),
  PASSWORD_SHOULD_BE_EQUAL("Passwords should be equal"),
  PASSWORD_IS_SHORT("Allowed password length should be from 3 to 12 characters");

  private final String msg;

  @Override
  public String getMessage() {
    return msg;
  }
}
