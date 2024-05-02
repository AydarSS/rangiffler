package project.qa.rangiffler.data.repository;

import guru.qa.grpc.rangiffler.PhotoOuterClass.Photo;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.qa.rangiffler.data.PhotoEntity;

@Repository
public interface PhotoRepository extends JpaRepository<PhotoEntity, UUID> {

  Slice<PhotoEntity> findByUsernameIn(Collection<String> usernames, @Nonnull Pageable pageable);

}
