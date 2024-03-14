package com.nimble.server_spring.modules.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import com.nimble.server_spring.IntegrationTestSupport;
import com.nimble.server_spring.infra.error.ErrorCode;
import com.nimble.server_spring.infra.error.ErrorCodeException;
import com.nimble.server_spring.infra.security.RoleType;
import com.nimble.server_spring.modules.auth.dto.request.LocalSignupServiceRequest;
import com.nimble.server_spring.modules.auth.dto.request.PublishTokenServiceRequest;
import com.nimble.server_spring.modules.auth.dto.response.JwtTokenResponse;
import com.nimble.server_spring.modules.auth.dto.response.UserResponse;
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

@Transactional
class AuthServiceTest extends IntegrationTestSupport {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtTokenRepository jwtTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @DisplayName("ID, 비밀번호 정보를 받아서 회원 가입을 한다.")
    @Test
    void signup() {
        // given
        String password = "03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4";
        LocalSignupServiceRequest localSignupRequest = LocalSignupServiceRequest.builder()
            .email("user@email.com")
            .password(password)
            .nickname("user1")
            .build();

        // when
        UserResponse userResponse = authService.signup(localSignupRequest);

        // then
        assertThat(userResponse)
            .extracting("email", "nickname", "providerType")
            .containsExactly("user@email.com", "user1", OauthProvider.LOCAL);

        List<User> users = userRepository.findAll();
        assertThat(users).hasSize(1)
            .extracting("email", "nickname", "providerType")
            .containsExactlyInAnyOrder(tuple("user@email.com", "user1", OauthProvider.LOCAL));
        assertThat(passwordEncoder.matches(password, users.get(0).getPassword())).isTrue();
    }

    @DisplayName("이미 가입한 이메일로 회원 가입을 하는 경우 예외가 발생한다.")
    @Test
    void signupWithExistingEmail() {
        // given
        User user = createLocalUser("user@email.com");
        userRepository.save(user);

        LocalSignupServiceRequest localSignupRequest = LocalSignupServiceRequest.builder()
            .email("user@email.com")
            .password("03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4")
            .nickname("user2")
            .build();

        // when
        // then
        assertThatThrownBy(() -> authService.signup(localSignupRequest))
            .isInstanceOf(ErrorCodeException.class)
            .hasMessage(ErrorCode.EMAIL_ALREADY_EXISTS.getMessage());
    }

    @DisplayName("기존에 토큰을 발급한 적이 없는 유저이면 토큰을 발급한 후 저장한다.")
    @Test
    void publishJwtTokenNotExisting() {
        // given
        User user = createLocalUser("user@email.com");
        userRepository.save(user);

        PublishTokenServiceRequest publishTokenRequest = PublishTokenServiceRequest.create(
            user.getId(),
            RoleType.USER
        );

        // when
        JwtTokenResponse jwtTokenResponse = authService.publishJwtToken(publishTokenRequest);

        // then
        assertThat(jwtTokenResponse).isNotNull()
            .extracting("accessToken", "refreshToken")
            .allSatisfy(token -> assertThat(token).isNotNull());
        assertThat(jwtTokenResponse.getUser().getEmail()).isEqualTo("user@email.com");

        List<JwtToken> jwtTokens = jwtTokenRepository.findAll();
        assertThat(jwtTokens).hasSize(1)
            .extracting("accessToken", "refreshToken")
            .containsExactlyInAnyOrder(
                tuple(jwtTokenResponse.getAccessToken(), jwtTokenResponse.getRefreshToken())
            );
    }

    @DisplayName("기존에 토큰을 발급한 적이 있는 유저이면 토큰을 발급한 후 기존의 데이터를 수정한다.")
    @Test
    void publishJwtTokenExisting() {
        // given
        User user = createLocalUser("user@email.com");
        userRepository.save(user);

        JwtToken jwtToken = createJwtToken(user);
        jwtTokenRepository.save(jwtToken);

        PublishTokenServiceRequest publishTokenRequest = PublishTokenServiceRequest.create(
            user.getId(),
            RoleType.USER
        );

        // when
        JwtTokenResponse jwtTokenResponse = authService.publishJwtToken(publishTokenRequest);

        // then
        assertThat(jwtTokenResponse).isNotNull()
            .extracting("accessToken", "refreshToken")
            .allSatisfy(token -> assertThat(token).isNotNull());
        assertThat(jwtTokenResponse.getUser().getEmail()).isEqualTo("user@email.com");

        List<JwtToken> jwtTokens = jwtTokenRepository.findAll();
        assertThat(jwtTokens).hasSize(1)
            .extracting("accessToken", "refreshToken")
            .containsExactlyInAnyOrder(
                tuple(jwtTokenResponse.getAccessToken(), jwtTokenResponse.getRefreshToken())
            );
    }


    private static User createLocalUser(String email) {
        return User.createLocalUser(
            email,
            "$2a$12$SE54wBhXbyHzeicLdzFK5OOvuJq0mg29ScXLIeDEjeQoGLlJylDb6",
            "user1"
        );
    }

    private static JwtToken createJwtToken(User user) {
        return JwtToken.builder()
            .accessToken("accessToken")
            .refreshToken("refreshToken")
            .expiresAt(LocalDateTime.now().plusDays(1))
            .user(user)
            .build();
    }
}