package project.qa.rangiffler.data.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import project.qa.rangiffler.data.FriendshipEntity;
import project.qa.rangiffler.data.UserEntity;

@Repository
public interface FriendshipRepository extends JpaRepository<FriendshipEntity, UUID> {

  @Query("""
      select f from FriendshipEntity f 
      where (f.requester = :requester and f.addressee = :addresse)
      """)
  Optional<FriendshipEntity> findFriendship(
      @Param("requester") UserEntity requester,
      @Param("addresse") UserEntity addresse);

}
