package project.qa.rangiffler.data.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.qa.rangiffler.data.LikeEntity;

@Repository
public interface LikeRepository extends JpaRepository<LikeEntity, UUID> {

}
