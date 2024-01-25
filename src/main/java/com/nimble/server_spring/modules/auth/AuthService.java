package com.nimble.server_spring.modules.auth;

import com.nimble.server_spring.infra.error.ErrorCode;
import com.nimble.server_spring.infra.error.ErrorCodeException;
import com.nimble.server_spring.infra.jwt.AuthToken;
import com.nimble.server_spring.infra.jwt.AuthTokenManager;
import com.nimble.server_spring.infra.jwt.JwtTokenType;
import com.nimble.server_spring.infra.security.RoleType;
import com.nimble.server_spring.modules.auth.dto.request.LocalSignupRequestDto;
import com.nimble.server_spring.modules.auth.enums.OauthProvider;
import com.nimble.server_spring.modules.user.User;
import com.nimble.server_spring.modules.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class AuthService {

    private final JwtTokenRepository jwtTokenRepository;
    private final UserRepository userRepository;
    private final AuthTokenManager authTokenManager;
    private final PasswordEncoder passwordEncoder;

    public User signup(LocalSignupRequestDto localSignupDto) {
        boolean isEmailAlreadyExists = userRepository.existsByEmail(
            localSignupDto.getEmail()
        );
        if (isEmailAlreadyExists) {
            throw new ErrorCodeException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        String encodedPassword = passwordEncoder.encode(localSignupDto.getPassword());

        User user = User.builder()
            .email(localSignupDto.getEmail())
            .password(encodedPassword)
            .nickname(localSignupDto.getNickname())
            .providerType(OauthProvider.LOCAL)
            .providerId(null)
            .build();

        return userRepository.save(user);
    }

    public JwtToken rotateRefreshToken(String prevRefreshToken, String prevAccessToken) {
        JwtToken jwtToken = jwtTokenRepository.findOneByRefreshToken(prevRefreshToken)
            .orElseThrow(() -> new ErrorCodeException(ErrorCode.INVALID_REFRESH_TOKEN));
        if (!jwtToken.equalsAccessToken(prevAccessToken)) {
            throw new ErrorCodeException(ErrorCode.INCONSISTENT_ACCESS_TOKEN);
        }
        boolean isRefreshTokenValid = authTokenManager
            .validateToken(prevRefreshToken, JwtTokenType.REFRESH);
        if (!isRefreshTokenValid) {
            throw new ErrorCodeException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        }

        AuthToken newAccessToken = authTokenManager.publishToken(
            jwtToken.getUser().getId(),
            RoleType.USER,
            JwtTokenType.ACCESS
        );
        AuthToken newRefreshToken = authTokenManager.publishToken(
            jwtToken.getUser().getId(),
            null,
            JwtTokenType.REFRESH
        );

        return jwtToken.reissue(newAccessToken, newRefreshToken);
    }
}
