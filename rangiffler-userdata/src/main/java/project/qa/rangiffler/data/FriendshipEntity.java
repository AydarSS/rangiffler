package project.qa.rangiffler.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@IdClass(FriendsId.class)
@Table(name = "friendship")
public class FriendshipEntity {

  @Id
  @ManyToOne
  @JoinColumn(name = "requester_id", referencedColumnName = "id")
  UserEntity requesterUser;

  @Id
  @ManyToOne
  @JoinColumn(name = "addressee_id", referencedColumnName = "id")
  UserEntity addresseeUser;

  @Column(name = "created_date", nullable = false)
  LocalDateTime createdDate;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  FriendshipStatus friendshipStatus;

}
