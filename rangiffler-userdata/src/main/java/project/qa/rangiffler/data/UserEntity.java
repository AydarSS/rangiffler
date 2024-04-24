package project.qa.rangiffler.data;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user")
public class UserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "username", nullable = false)
  String username;

  @Column(name = "firstname")
  String firstname;

  @Column(name = "lastname")
  String surname;

  @Column(name = "avatar")
  byte[] avatar;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "country_id", referencedColumnName = "id")
  CountryEntity country;

}
