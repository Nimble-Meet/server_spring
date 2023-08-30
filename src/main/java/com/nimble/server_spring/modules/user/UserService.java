package com.nimble.server_spring.modules.user;

import com.nimble.server_spring.infra.error.ErrorCode;
import com.nimble.server_spring.infra.error.ErrorCodeException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

  private final UserRepository userRepository;

  public User getUserByEmail(String email) {
    return userRepository.findOneByEmail(email).orElseThrow(
        () -> new ErrorCodeException(ErrorCode.USER_NOT_FOUND_BY_EMAIL));
  }
}