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
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class AuthService {

    private final JwtTokenRepository jwtTokenRepository;
    private final UserRepository userRepository;
    private final AuthTokenManager authTokenManager;

    @Transactional
    public User signup(LocalSignupRequestDto localSignupDto) {
        boolean isEmailAlreadyExists = userRepository.existsByEmail(
            localSignupDto.getEmail()
        );
        if (isEmailAlreadyExists) {
            throw new ErrorCodeException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        EncryptedPassword encryptedPassword = EncryptedPassword.encryptFrom(
            localSignupDto.getPassword()
        );

        User user = User.builder()
            .email(localSignupDto.getEmail())
            .nickname(localSignupDto.getNickname())
            .password(encryptedPassword.getPassword())
            .providerType(OauthProvider.LOCAL)
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
            .getTokenClaims(prevRefreshToken, JwtTokenType.REFRESH)
            .isPresent();
        if (!isRefreshTokenValid) {
            throw new ErrorCodeException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        }

        AuthToken newAccessToken = authTokenManager.publishToken(
            jwtToken.getUser().getEmail(),
            RoleType.USER.getCode(),
            JwtTokenType.ACCESS
        );
        AuthToken newRefreshToken = authTokenManager.publishToken(
            jwtToken.getUser().getEmail(),
            null,
            JwtTokenType.REFRESH
        );

        JwtToken newJwtToken = jwtToken.reissue(newAccessToken, newRefreshToken);
        return jwtTokenRepository.save(newJwtToken);
    }

    public User getCurrentUser() {
        Object principal = Optional
            .ofNullable(SecurityContextHolder.getContext().getAuthentication())
            .map(Authentication::getPrincipal)
            .orElseThrow(() -> new ErrorCodeException(ErrorCode.UNAUTHENTICATED_REQUEST));

        if (!(principal instanceof UserDetails)) {
            Error customError = new Error(
                "principal is not instance of UserDetails, it is" + principal.getClass()
            );
            throw new ErrorCodeException(ErrorCode.INTERNAL_SERVER_ERROR, customError);
        }
        String email = ((UserDetails) principal).getUsername();

        return userRepository.findOneByEmail(email).orElseThrow(
            () -> new ErrorCodeException(ErrorCode.USER_NOT_FOUND)
        );
    }
}
