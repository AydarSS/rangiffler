package project.qa.rangiffler.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import project.qa.rangiffler.data.UserEntity;
import project.qa.rangiffler.data.repository.UserRepository;
import project.qa.rangiffler.domain.RangifflerUserPrincipal;

@Component
public class RanfifflerUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  @Autowired
  public RanfifflerUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    UserEntity user = userRepository.findByUsername(username);
    if (user == null) {
      throw new UsernameNotFoundException("Username: " + username + " not found");
    }
    return new RangifflerUserPrincipal(user);
  }
}
