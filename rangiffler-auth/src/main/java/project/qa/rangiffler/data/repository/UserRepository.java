package project.qa.rangiffler.data.repository;


import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;
import project.qa.rangiffler.data.Authority;
import project.qa.rangiffler.data.AuthorityEntity;
import project.qa.rangiffler.data.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {

  @Nullable
  UserEntity findByUsername(@Nonnull String username);


  class Fake implements UserRepository {

    private final List<UserEntity> userEntities = new ArrayList<>();

    public Fake() {
      userEntities.add(fakeuserEntity());
    }

    private UserEntity fakeuserEntity() {
      UserEntity testUserEntity = new UserEntity();

      List<AuthorityEntity> authorityEntities;
      AuthorityEntity read = new AuthorityEntity();
      read.setUser(testUserEntity);
      read.setAuthority(Authority.read);
      AuthorityEntity write = new AuthorityEntity();
      write.setUser(testUserEntity);
      write.setAuthority(Authority.write);
      authorityEntities = List.of(read, write);

      testUserEntity.setUsername("correct");
      testUserEntity.setAuthorities(authorityEntities);
      testUserEntity.setEnabled(true);
      testUserEntity.setPassword("test-pass");
      testUserEntity.setAccountNonExpired(true);
      testUserEntity.setAccountNonLocked(true);
      testUserEntity.setCredentialsNonExpired(true);
      testUserEntity.setId(UUID.randomUUID());
      return testUserEntity;
    }

    @Override
    public UserEntity findByUsername(String username) {
      return userEntities
          .stream()
          .filter(user -> user.getUsername().equals(username))
          .findFirst()
          .orElse(null);
    }

    @Override
    public void deleteInBatch(Iterable<UserEntity> entities) {
      UserRepository.super.deleteInBatch(entities);
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends UserEntity> S saveAndFlush(S entity) {
      return null;
    }

    @Override
    public <S extends UserEntity> List<S> saveAllAndFlush(Iterable<S> entities) {
      return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<UserEntity> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<UUID> uuids) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public UserEntity getOne(UUID uuid) {
      return null;
    }

    @Override
    public UserEntity getById(UUID uuid) {
      return null;
    }

    @Override
    public UserEntity getReferenceById(UUID uuid) {
      return null;
    }

    @Override
    public <S extends UserEntity> List<S> findAll(Example<S> example) {
      return null;
    }

    @Override
    public <S extends UserEntity> List<S> findAll(Example<S> example, Sort sort) {
      return null;
    }

    @Override
    public <S extends UserEntity> List<S> saveAll(Iterable<S> entities) {
      return null;
    }

    @Override
    public List<UserEntity> findAll() {
      return null;
    }

    @Override
    public List<UserEntity> findAllById(Iterable<UUID> uuids) {
      return null;
    }

    @Override
    public <S extends UserEntity> S save(S entity) {
      return null;
    }

    @Override
    public Optional<UserEntity> findById(UUID uuid) {
      return Optional.empty();
    }

    @Override
    public boolean existsById(UUID uuid) {
      return false;
    }

    @Override
    public long count() {
      return 0;
    }

    @Override
    public void deleteById(UUID uuid) {

    }

    @Override
    public void delete(UserEntity entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends UUID> uuids) {

    }

    @Override
    public void deleteAll(Iterable<? extends UserEntity> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<UserEntity> findAll(Sort sort) {
      return null;
    }

    @Override
    public Page<UserEntity> findAll(Pageable pageable) {
      return null;
    }

    @Override
    public <S extends UserEntity> Optional<S> findOne(Example<S> example) {
      return Optional.empty();
    }

    @Override
    public <S extends UserEntity> Page<S> findAll(Example<S> example, Pageable pageable) {
      return null;
    }

    @Override
    public <S extends UserEntity> long count(Example<S> example) {
      return 0;
    }

    @Override
    public <S extends UserEntity> boolean exists(Example<S> example) {
      return false;
    }

    @Override
    public <S extends UserEntity, R> R findBy(Example<S> example,
        Function<FetchableFluentQuery<S>, R> queryFunction) {
      return null;
    }
  }
}
