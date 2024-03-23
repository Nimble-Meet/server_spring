package com.nimble.server_spring.modules.auth;

import com.nimble.server_spring.IntegrationTestSupport;
import com.nimble.server_spring.modules.auth.enums.OauthProvider;
import com.nimble.server_spring.modules.user.User;
import com.nimble.server_spring.modules.user.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class JwtTokenRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private JwtTokenRepository jwtTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @DisplayName("refresh token 값으로 JWT Token 정보를 조회한다.")
    @Test
    void findOneByRefreshToken() {
        // given
        User user1 = createUser("user1@email.com");
        User user2 = createUser("user2@email.com");
        userRepository.saveAll(List.of(user1, user2));
        JwtToken jwtToken1 = createJwtToken(user1, "access_token1", "refresh_token1");
        JwtToken jwtToken2 = createJwtToken(user2, "access_token2", "refresh_token2");
        jwtTokenRepository.saveAll(List.of(jwtToken1, jwtToken2));

        // when
        JwtToken jwtToken = jwtTokenRepository.findOneByRefreshToken("refresh_token1")
            .orElse(null);

        // then
        assertThat(jwtToken).isNotNull()
            .extracting("refreshToken", "accessToken")
            .containsExactlyInAnyOrder("refresh_token1", "access_token1");
    }

    @DisplayName("사용자의 ID로 JWT Token 정보를 조회한다.")
    @Test
    void findOneByUserId() {
        // given
        User user1 = createUser("user1@email.com");
        User user2 = createUser("user2@email.com");
        userRepository.saveAll(List.of(user1, user2));
        Long userId = user1.getId();
        JwtToken jwtToken1 = createJwtToken(user1, "access_token1", "refresh_token1");
        JwtToken jwtToken2 = createJwtToken(user2, "access_token2", "refresh_token2");
        jwtTokenRepository.saveAll(List.of(jwtToken1, jwtToken2));

        // when
        JwtToken jwtToken = jwtTokenRepository.findOneByUserId(userId)
            .orElse(null);

        // then
        assertThat(jwtToken).isNotNull()
            .extracting("refreshToken", "accessToken")
            .containsExactlyInAnyOrder("refresh_token1", "access_token1");
    }

    private User createUser(String email) {
        return User.builder()
            .email(email)
            .password(passwordEncoder.encode("password"))
            .nickname("nickname")
            .providerType(OauthProvider.LOCAL)
            .build();
    }

    private JwtToken createJwtToken(User user1, String accessToken, String refreshToken) {
        return JwtToken.builder()
            .user(user1)
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .expiresAt(LocalDateTime.now().plusMinutes(30))
            .build();
    }
}