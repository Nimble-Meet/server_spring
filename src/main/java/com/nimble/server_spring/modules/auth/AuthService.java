package com.nimble.server_spring.modules.auth;

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

        User findUser = userRepository.findOneByEmail(localLoginDto.getEmail());
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
            throw new RuntimeException(AuthErrorMessages.INVALID_REFRESH_TOKEN.getMessage());
        }
        if (!jwtToken.equalsAccessToken(prevAccessToken)) {
            throw new RuntimeException(AuthErrorMessages.INCONSISTENT_ACCESS_TOKEN.getMessage());
        }

        AuthToken refreshToken = authTokenProvider.createRefreshTokenOf(prevRefreshToken);
        if(!refreshToken.validate()) {
            throw new RuntimeException(AuthErrorMessages.EXPIRED_REFRESH_TOKEN.getMessage());
        }

        AuthToken newAccessToken = authTokenProvider.publishAccessToken(jwtToken.getUser().getEmail(), RoleType.USER.getCode());
        AuthToken newRefreshToken = authTokenProvider.publishRefreshToken(jwtToken.getUser().getEmail());

        JwtToken newJwtToken = JwtToken.builder()
                .id(jwtToken.getId())
                .accessToken(newAccessToken.getToken())
                .refreshToken(newRefreshToken.getToken())
                .expiresAt(newRefreshToken.getExpiresAt())
                .user(jwtToken.getUser())
                .build();
        return jwtTokenRepository.save(newJwtToken);
    }
}
