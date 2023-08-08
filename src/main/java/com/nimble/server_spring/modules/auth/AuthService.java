package com.nimble.server_spring.modules.auth;

import com.nimble.server_spring.modules.auth.dto.request.LocalSignupRequestDto;
import com.nimble.server_spring.modules.auth.enums.OauthProvider;
import com.nimble.server_spring.modules.user.User;
import com.nimble.server_spring.modules.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final JwtTokenRepository jwtTokenRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public User signup(LocalSignupRequestDto localSignupDto) {
        boolean isEmailAlreadyExists = userRepository.existsByEmail(
                localSignupDto.getEmail()
        );
        if (isEmailAlreadyExists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, AuthErrorMessages.EMAIL_ALREADY_EXISTS.getMessage());
        }

        EncryptedPassword encryptedPassword = EncryptedPassword.encryptFrom(localSignupDto.getPassword());

        User user = User.builder()
                .email(localSignupDto.getEmail())
                .nickname(localSignupDto.getNickname())
                .password(encryptedPassword.getPassword())
                .providerType(OauthProvider.LOCAL)
                .build();

        return userRepository.save(user);
    }
}
