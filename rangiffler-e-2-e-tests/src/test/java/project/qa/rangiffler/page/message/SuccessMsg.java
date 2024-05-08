package project.qa.rangiffler.page.message;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SuccessMsg implements Msg {
  POST_CREATED("New post created"),
  POST_UPDATED("Post updated"),
  INVITATION_ACCEPTED("Invitation accepted"),
  INVITATION_DECLINED("Invitation declined"),
  SPENDING_ADDED("Spending successfully added"),
  INVITATION_SENT("Invitation sent"),
  PROFILE_UPDATED("Your profile is successfully updated");

  private final String msg;

  @Override
  public String getMessage() {
    return msg;
  }
}
