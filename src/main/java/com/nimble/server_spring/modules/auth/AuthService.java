package com.nimble.server_spring.modules.auth;

import com.nimble.server_spring.infra.error.ErrorCode;
import com.nimble.server_spring.infra.error.ErrorCodeException;
import com.nimble.server_spring.infra.jwt.AuthToken;
import com.nimble.server_spring.infra.jwt.AuthTokenManager;
import com.nimble.server_spring.infra.security.RoleType;
import com.nimble.server_spring.modules.auth.dto.request.LocalSignupServiceRequest;
import com.nimble.server_spring.modules.auth.dto.request.PublishTokenServiceRequest;
import com.nimble.server_spring.modules.auth.dto.request.RotateTokenServiceRequest;
import com.nimble.server_spring.modules.auth.dto.response.JwtTokenResponse;
import com.nimble.server_spring.modules.auth.dto.response.UserResponse;
import com.nimble.server_spring.modules.user.User;
import com.nimble.server_spring.modules.user.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
@Validated
public class AuthService {

    private final JwtTokenRepository jwtTokenRepository;
    private final UserRepository userRepository;
    private final AuthTokenManager authTokenManager;
    private final PasswordEncoder passwordEncoder;

    public UserResponse signup(@Valid LocalSignupServiceRequest localSignupRequest) {
        boolean isEmailAlreadyExists = userRepository.existsByEmail(
            localSignupRequest.getEmail()
        );
        if (isEmailAlreadyExists) {
            throw new ErrorCodeException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        String encodedPassword = passwordEncoder.encode(localSignupRequest.getPassword());

        User user = User.createLocalUser(
            localSignupRequest.getEmail(),
            encodedPassword,
            localSignupRequest.getNickname()
        );
        return UserResponse.fromUser(userRepository.save(user));
    }

    public JwtTokenResponse publishJwtToken(@Valid PublishTokenServiceRequest publicTokenRequest) {
        AuthToken accessToken = authTokenManager.publishAccessToken(
            publicTokenRequest.getUserId(),
            publicTokenRequest.getRoleType()
        );
        AuthToken refreshToken = authTokenManager.publishRefreshToken(
            publicTokenRequest.getUserId()
        );

        JwtToken jwtToken = issueOrReissueJwtToken(
            publicTokenRequest.getUserId(),
            accessToken,
            refreshToken
        );
        return JwtTokenResponse.fromJwtToken(jwtToken);
    }

    public JwtTokenResponse rotateToken(@Validated RotateTokenServiceRequest rotateTokenRequest) {
        validateRefreshToken(rotateTokenRequest.getRefreshToken());
        JwtToken jwtToken = validateAndGetJwtToken(
            rotateTokenRequest.getAccessToken(),
            rotateTokenRequest.getRefreshToken()
        );

        AuthToken newAccessToken = authTokenManager.publishAccessToken(
            jwtToken.getUser().getId(),
            RoleType.USER
        );
        AuthToken newRefreshToken = authTokenManager.publishRefreshToken(
            jwtToken.getUser().getId()
        );

        jwtToken.reissue(newAccessToken, newRefreshToken);
        return JwtTokenResponse.fromJwtToken(jwtToken);
    }

    private JwtToken issueOrReissueJwtToken(
        Long userId,
        AuthToken accessToken,
        AuthToken refreshToken
    ) {
        return jwtTokenRepository.findOneByUserId(userId)
            .map(token -> token.reissue(accessToken, refreshToken))
            .orElseGet(() -> {
                User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ErrorCodeException(ErrorCode.USER_NOT_FOUND));
                JwtToken newToken = JwtToken.issue(accessToken, refreshToken, user);
                return jwtTokenRepository.save(newToken);
            });
    }

    private void validateRefreshToken(String refreshToken) {
        boolean isRefreshTokenValid = authTokenManager.isValidRefreshToken(
            refreshToken
        );
        if (!isRefreshTokenValid) {
            throw new ErrorCodeException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
    }

    private JwtToken validateAndGetJwtToken(String accessToken, String refreshToken) {
        JwtToken jwtToken = jwtTokenRepository
            .findOneByRefreshToken(refreshToken)
            .orElseThrow(() -> new ErrorCodeException(ErrorCode.INVALID_REFRESH_TOKEN));
        if (!jwtToken.equalsAccessToken(accessToken)) {
            throw new ErrorCodeException(ErrorCode.INCONSISTENT_ACCESS_TOKEN);
        }
        return jwtToken;
    }
}
