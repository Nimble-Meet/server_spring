package com.nimble.server_spring.modules.auth;

import com.nimble.server_spring.infra.error.ErrorCode;
import com.nimble.server_spring.infra.error.ErrorCodeException;
import com.nimble.server_spring.infra.jwt.AuthToken;
import com.nimble.server_spring.infra.jwt.AuthTokenManager;
import com.nimble.server_spring.infra.jwt.JwtTokenType;
import com.nimble.server_spring.infra.security.RoleType;
import com.nimble.server_spring.modules.auth.dto.request.LocalSignupRequestDto;
import com.nimble.server_spring.modules.auth.dto.response.JwtTokenResponse;
import com.nimble.server_spring.modules.auth.dto.response.UserResponse;
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

    public UserResponse signup(LocalSignupRequestDto localSignupDto) {
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

        return UserResponse.fromUser(userRepository.save(user));
    }

    public JwtTokenResponse publishJwtToken(Long userId, RoleType roleType) {
        AuthToken accessToken = authTokenManager.publishToken(
            userId,
            roleType,
            JwtTokenType.ACCESS
        );
        AuthToken refreshToken = authTokenManager.publishToken(
            userId,
            null,
            JwtTokenType.REFRESH
        );

        JwtToken jwtToken = jwtTokenRepository.findOneByUserId(userId)
            .map(token -> token.reissue(accessToken, refreshToken))
            .orElseGet(() -> {
                User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ErrorCodeException(ErrorCode.USER_NOT_FOUND));
                JwtToken newToken = JwtToken.issue(accessToken, refreshToken, user);
                return jwtTokenRepository.save(newToken);
            });
        return JwtTokenResponse.fromJwtToken(jwtToken);
    }

    public JwtTokenResponse rotateRefreshToken(String prevRefreshToken, String prevAccessToken) {
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

        jwtToken.reissue(newAccessToken, newRefreshToken);
        return JwtTokenResponse.fromJwtToken(jwtToken);
    }
}
