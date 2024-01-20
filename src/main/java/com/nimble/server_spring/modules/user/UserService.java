package com.nimble.server_spring.modules.user;

import com.nimble.server_spring.infra.error.ErrorCode;
import com.nimble.server_spring.infra.error.ErrorCodeException;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public User getUserByEmail(String email) {
        return userRepository.findOneByEmail(email).orElseThrow(
            () -> new ErrorCodeException(ErrorCode.USER_NOT_FOUND_BY_EMAIL));
    }

    public User getUserByPrincipal(Principal principal) {
        Long userId = Long.parseLong(principal.getName());
        return userRepository.findById(userId).orElseThrow(
            () -> new ErrorCodeException(ErrorCode.USER_NOT_FOUND)
        );
    }

    public User getUserByPrincipalLazy(Principal principal) {
        Long userId = Long.parseLong(principal.getName());
        return userRepository.getReferenceById(userId);
    }
}
