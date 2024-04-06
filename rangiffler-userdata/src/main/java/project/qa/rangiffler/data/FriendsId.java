package project.qa.rangiffler.data;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class FriendsId implements Serializable {

  private UUID requesterUser;
  private UUID addresseeUser;

  public UUID getRequesterUser() {
    return requesterUser;
  }

  public void setRequesterUser(UUID requesterUser) {
    this.requesterUser = requesterUser;
  }

  public UUID getAddresseeUser() {
    return addresseeUser;
  }

  public void setAddresseeUser(UUID addresseeUser) {
    this.addresseeUser = addresseeUser;
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
    return Objects.equals(requesterUser, friendsId.requesterUser) && Objects.equals(addresseeUser,
        friendsId.addresseeUser);
  }

  @Override
  public int hashCode() {
    return Objects.hash(requesterUser, addresseeUser);
  }
}
