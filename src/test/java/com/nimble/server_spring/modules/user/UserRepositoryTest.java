package com.nimble.server_spring.modules.user;

import static org.assertj.core.api.Assertions.assertThat;

import com.nimble.server_spring.IntegrationTestSupport;
import com.nimble.server_spring.modules.auth.enums.OauthProvider;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class UserRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @DisplayName("email로 User를 찾는다.")
    @Test
    void findOneByEmail() {
        // given
        User user1 = createUser("user1@email.com");
        User user2 = createUser("user2@email.com");
        userRepository.saveAll(List.of(user1, user2));

        // when
        User user = userRepository.findOneByEmail("user1@email.com")
            .orElse(null);

        // then
        assertThat(user).isNotNull()
            .extracting("email")
            .isEqualTo("user1@email.com");
    }

    @DisplayName("email로 User의 존재 여부를 확인한다.")
    @CsvSource({"user@email.com,true", "User@email.com,false"})
    @ParameterizedTest
    void existsByEmail(String email, boolean expected) {
        // given
        User user = createUser("user@email.com");
        userRepository.save(user);

        // when
        boolean existsByEmail = userRepository.existsByEmail(email);

        // then
        assertThat(existsByEmail).isEqualTo(expected);
    }

    private User createUser(String email) {
        return User.builder()
            .email(email)
            .password(passwordEncoder.encode("password"))
            .nickname("nickname")
            .providerType(OauthProvider.LOCAL)
            .build();
    }
}