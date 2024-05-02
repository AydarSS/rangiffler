package project.qa.rangiffler.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "photo")
public class PhotoEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "username", nullable = false)
  private String username;

  @Column(name = "photo", nullable = false)
  private String photo;

  @Column(name = "country_code", nullable = false)
  private String countryCode;

  @Column(name = "created_date", nullable = false)
  private LocalDateTime createdDate;

  @Column(name = "description")
  private String description;



}
