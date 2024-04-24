package project.qa.rangiffler.service;

import guru.qa.grpc.rangiffler.UserOuterClass.LinkedUsersByUsernameRequest;
import guru.qa.grpc.rangiffler.UserOuterClass.UserByUsernameRequest;
import guru.qa.grpc.rangiffler.UserOuterClass.UserByUsernameResponse;
import guru.qa.grpc.rangiffler.UserOuterClass.UsersPageableResponse;
import guru.qa.grpc.rangiffler.UserServiceGrpc;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import java.util.Optional;
import java.util.function.Consumer;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import project.qa.rangiffler.data.UserEntity;
import project.qa.rangiffler.data.repository.UserRepository;
import project.qa.rangiffler.data.utils.UserEntityGrpcConverter;

@GrpcService
public class GrpcUserDataSevice extends UserServiceGrpc.UserServiceImplBase {

  private final UserRepository userRepository;
  private final String STRING_EMPTY = "";

  @Autowired
  public GrpcUserDataSevice(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public void getAllUsers(LinkedUsersByUsernameRequest request,
      StreamObserver<UsersPageableResponse> responseObserver) {
    UsersPageableResponse response = getPagebleUsers(
        () -> userRepository.findByUsernameNot(
            request.getUsername(),
            PageRequest.of(request.getPageInfo().getPage(), request.getPageInfo().getSize()))
    );

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void getUserByUsername(UserByUsernameRequest request,
      StreamObserver<UserByUsernameResponse> responseObserver) {
    Optional<UserEntity> user = userRepository.findByUsername(request.getUsername());
    if (user.isPresent()) {
      UserByUsernameResponse response = UserByUsernameResponse.newBuilder()
          .setUser(UserEntityGrpcConverter.fromUserEntity(user.get()))
          .build();
      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } else {
      responseObserver.onError(Status.INVALID_ARGUMENT
          .withDescription(String.format("User %s not found",
              request.getUsername()))
          .asRuntimeException());
    }

  }

  @Override
  public void getFriends(LinkedUsersByUsernameRequest request,
      StreamObserver<UsersPageableResponse> responseObserver) {
    UserEntity user = buildUserEntityFromUsername(request.getUsername());
    UsersPageableResponse response;
    if (isSearchQueryIsEmptyIn(request) && isPageableIsNotExistsIn(request)) {
      response = getPagebleUsers(
          () -> userRepository.findFriends(user));
    } else if (isSearchQueryIsEmptyIn(request)) {
      response = getPagebleUsers(
          () -> userRepository.findFriends(user,
              PageRequest.of(request.getPageInfo().getPage(), request.getPageInfo().getSize())));
    } else if (isPageableIsNotExistsIn(request)) {
      response = getPagebleUsers(
          () -> userRepository.findFriends(user,
              request.getPageInfo().getSearchQuery()));
    } else {
      response = getPagebleUsers(
          () -> userRepository.findFriends(user,
              PageRequest.of(request.getPageInfo().getPage(), request.getPageInfo().getSize()),
              request.getPageInfo().getSearchQuery()));
    }

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void getIncomeInvitations(LinkedUsersByUsernameRequest request,
      StreamObserver<UsersPageableResponse> responseObserver) {
    UserEntity user = buildUserEntityFromUsername(request.getUsername());
    UsersPageableResponse response;
    if (isSearchQueryIsEmptyIn(request) && isPageableIsNotExistsIn(request)) {
      response = getPagebleUsers(
          () -> userRepository.findIncomeInvitations(user));
    } else if (isSearchQueryIsEmptyIn(request)) {
      response = getPagebleUsers(
          () -> userRepository.findIncomeInvitations(user,
              PageRequest.of(request.getPageInfo().getPage(), request.getPageInfo().getSize())));
    } else if (isPageableIsNotExistsIn(request)) {
      response = getPagebleUsers(
          () -> userRepository.findIncomeInvitations(user,
              request.getPageInfo().getSearchQuery()));
    } else {
      response = getPagebleUsers(
          () -> userRepository.findIncomeInvitations(user,
              PageRequest.of(request.getPageInfo().getPage(), request.getPageInfo().getSize()),
              request.getPageInfo().getSearchQuery()));
    }

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void getOutcomeInvitations(LinkedUsersByUsernameRequest request,
      StreamObserver<UsersPageableResponse> responseObserver) {
    UserEntity user = buildUserEntityFromUsername(request.getUsername());
    UsersPageableResponse response;
    if (isSearchQueryIsEmptyIn(request) && isPageableIsNotExistsIn(request)) {
      response = getPagebleUsers(
          () -> userRepository.findOutcomeInvitations(user));
    } else if (isSearchQueryIsEmptyIn(request)) {
      response = getPagebleUsers(
          () -> userRepository.findOutcomeInvitations(user,
              PageRequest.of(request.getPageInfo().getPage(), request.getPageInfo().getSize())));
    } else if (isPageableIsNotExistsIn(request)) {
      response = getPagebleUsers(
          () -> userRepository.findOutcomeInvitations(user,
              request.getPageInfo().getSearchQuery()));
    } else {
      response = getPagebleUsers(
          () -> userRepository.findOutcomeInvitations(user,
              PageRequest.of(request.getPageInfo().getPage(), request.getPageInfo().getSize()),
              request.getPageInfo().getSearchQuery()));
    }

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  private UsersPageableResponse getPagebleUsers(UsersResponse<Slice<UserEntity>> request) {

    Slice<UserEntity> users = request.get();

    return UsersPageableResponse.newBuilder()
        .addAllUsers(users
            .getContent()
            .stream()
            .map(UserEntityGrpcConverter::fromUserEntity)
            .toList())
        .setHasNext(users.hasNext())
        .build();

  }

  private boolean isSearchQueryIsEmptyIn(LinkedUsersByUsernameRequest request) {
    return request.getPageInfo().getSearchQuery().equals(STRING_EMPTY);
  }

  private boolean isPageableIsNotExistsIn(LinkedUsersByUsernameRequest request) {
    return request.getPageInfo().getPage() == 0 && request.getPageInfo().getSize() == 0;
  }

  private UserEntity buildUserEntityFromUsername(String username) {
    return userRepository.findByUsername(username).get();
  }
}
