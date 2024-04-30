package project.qa.rangiffler.data.repository;

import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.qa.rangiffler.data.CountryEntity;

@Repository
public interface CountryRepository extends JpaRepository<CountryEntity, UUID> {

  Optional<CountryEntity> findByCode(@Nonnull String code);

}
