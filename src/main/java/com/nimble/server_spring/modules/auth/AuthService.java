package com.nimble.server_spring.modules.auth;

import com.nimble.server_spring.infra.error.ErrorCode;
import com.nimble.server_spring.infra.error.ErrorCodeException;
import com.nimble.server_spring.infra.jwt.AuthToken;
import com.nimble.server_spring.infra.jwt.AuthTokenProvider;
import com.nimble.server_spring.infra.security.RoleType;
import com.nimble.server_spring.infra.security.UserPrincipal;
import com.nimble.server_spring.modules.auth.dto.request.LocalLoginRequestDto;
import com.nimble.server_spring.modules.auth.dto.request.LocalSignupRequestDto;
import com.nimble.server_spring.modules.auth.enums.OauthProvider;
import com.nimble.server_spring.modules.user.User;
import com.nimble.server_spring.modules.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final JwtTokenRepository jwtTokenRepository;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final AuthTokenProvider authTokenProvider;

    @Transactional
    public User signup(LocalSignupRequestDto localSignupDto) {
        boolean isEmailAlreadyExists = userRepository.existsByEmail(
            localSignupDto.getEmail()
        );
        if (isEmailAlreadyExists) {
            throw new ErrorCodeException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        EncryptedPassword encryptedPassword = EncryptedPassword.encryptFrom(
            localSignupDto.getPassword());

        User user = User.builder()
            .email(localSignupDto.getEmail())
            .nickname(localSignupDto.getNickname())
            .password(encryptedPassword.getPassword())
            .providerType(OauthProvider.LOCAL)
            .build();

        return userRepository.save(user);
    }

    @Transactional
    public JwtToken jwtSign(LocalLoginRequestDto localLoginDto) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                localLoginDto.getEmail(),
                localLoginDto.getPassword()
            )
        );
        String role = ((UserPrincipal) authentication.getPrincipal()).getRoleType().getCode();

        AuthToken accessToken = authTokenProvider.publishAccessToken(
            localLoginDto.getEmail(),
            role
        );
        AuthToken refreshToken = authTokenProvider.publishRefreshToken(localLoginDto.getEmail());

        User findUser = userRepository.findOneByEmail(localLoginDto.getEmail()).orElseThrow(
            () -> new ErrorCodeException(ErrorCode.USER_NOT_FOUND));
        JwtToken findJwtToken = jwtTokenRepository.findOneByUserId(findUser.getId());
        JwtToken jwtToken = JwtToken.builder()
            .id(findJwtToken != null ? findJwtToken.getId() : null)
            .accessToken(accessToken.getToken())
            .refreshToken(refreshToken.getToken())
            .expiresAt(refreshToken.getExpiresAt())
            .user(findUser)
            .build();

        return jwtTokenRepository.save(jwtToken);
    }

    public JwtToken rotateRefreshToken(String prevRefreshToken, String prevAccessToken) {
        JwtToken jwtToken = jwtTokenRepository.findOneByRefreshToken(prevRefreshToken);
        if (jwtToken == null) {
            throw new ErrorCodeException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
        if (!jwtToken.equalsAccessToken(prevAccessToken)) {
            throw new ErrorCodeException(ErrorCode.INCONSISTENT_ACCESS_TOKEN);
        }

        AuthToken refreshToken = authTokenProvider.createRefreshTokenOf(prevRefreshToken);
        if (!refreshToken.validate()) {
            throw new ErrorCodeException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        }

        AuthToken newAccessToken = authTokenProvider.publishAccessToken(
            jwtToken.getUser().getEmail(),
            RoleType.USER.getCode()
        );
        AuthToken newRefreshToken = authTokenProvider.publishRefreshToken(
            jwtToken.getUser().getEmail());

        JwtToken newJwtToken = JwtToken.builder()
            .id(jwtToken.getId())
            .accessToken(newAccessToken.getToken())
            .refreshToken(newRefreshToken.getToken())
            .expiresAt(newRefreshToken.getExpiresAt())
            .user(jwtToken.getUser())
            .build();
        return jwtTokenRepository.save(newJwtToken);
    }

    public User getCurrentUser() {
        final Authentication authentication = SecurityContextHolder.getContext()
            .getAuthentication();

        if (authentication == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        String username = null;
        if (authentication.getPrincipal() instanceof UserDetails springSecurityUser) {
            username = springSecurityUser.getUsername();
        } else if (authentication.getPrincipal() instanceof String) {
            username = (String) authentication.getPrincipal();
        }

        return userRepository.findOneByEmail(username).orElseThrow(
            () -> new ErrorCodeException(ErrorCode.USER_NOT_FOUND)
        );
    }
}
