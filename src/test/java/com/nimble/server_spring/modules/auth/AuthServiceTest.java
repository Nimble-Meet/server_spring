package com.nimble.server_spring.modules.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.*;

import com.nimble.server_spring.IntegrationTestSupport;
import com.nimble.server_spring.infra.error.ErrorCode;
import com.nimble.server_spring.infra.error.ErrorCodeException;
import com.nimble.server_spring.modules.auth.dto.request.LocalSignupServiceRequest;
import com.nimble.server_spring.modules.auth.dto.response.UserResponse;
import com.nimble.server_spring.modules.auth.enums.OauthProvider;
import com.nimble.server_spring.modules.user.User;
import com.nimble.server_spring.modules.user.UserRepository;
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
        User user = User.createLocalUser(
            "user@email.com",
            "$2a$12$SE54wBhXbyHzeicLdzFK5OOvuJq0mg29ScXLIeDEjeQoGLlJylDb6",
            "user1"
        );
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
}