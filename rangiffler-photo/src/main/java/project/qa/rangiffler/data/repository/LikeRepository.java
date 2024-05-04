package project.qa.rangiffler.data.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.qa.rangiffler.data.LikeEntity;
import project.qa.rangiffler.data.PhotoEntity;

@Repository
public interface LikeRepository extends JpaRepository<LikeEntity, UUID> {

  Optional<LikeEntity> findByUserIdAndUsernameAndPhotoId (UUID userid, String username, UUID photo);

  Optional<List<LikeEntity>> findByPhotoId (UUID photoId);

}
