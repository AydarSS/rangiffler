package project.qa.rangiffler.data;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class FriendsId implements Serializable {

  private UUID requester;
  private UUID addressee;

  public UUID getRequester() {
    return requester;
  }

  public void setRequester(UUID requesterUser) {
    this.requester = requesterUser;
  }

  public UUID getAddressee() {
    return addressee;
  }

  public void setAddressee(UUID addresseeUser) {
    this.addressee = addresseeUser;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FriendsId friendsId = (FriendsId) o;
    return Objects.equals(requester, friendsId.requester) && Objects.equals(addressee,
        friendsId.addressee);
  }

  @Override
  public int hashCode() {
    return Objects.hash(requester, addressee);
  }
}
