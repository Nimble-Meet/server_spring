package com.nimble.server_spring.modules.meet;

import com.nimble.server_spring.IntegrationTestSupport;
import com.nimble.server_spring.modules.auth.enums.OauthProvider;
import com.nimble.server_spring.modules.user.User;
import com.nimble.server_spring.modules.user.UserRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

@Transactional
class MeetRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private MeetRepository meetRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MeetUserRepository meetUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @DisplayName("id로 Meet을 찾는다.")
    @Test
    void findMeetById() {
        // given
        Meet meet1 = createMeet("meetName1");
        Meet meet2 = createMeet("meetName2");
        meetRepository.saveAll(List.of(meet1, meet2));

        // when
        Meet meet = meetRepository.findMeetById(meet1.getId())
            .orElse(null);

        // then
        assertThat(meet).isNotNull()
            .extracting("meetName", "code")
            .containsExactlyInAnyOrder("meetName1", meet1.getCode());
    }

    @DisplayName("해당하는 User가 참여한 Meet 목록을 찾는다.")
    @Test
    void findParticipatedMeets() {
        // given
        User user = createUser("user@email.com");
        userRepository.save(user);

        Meet meet1 = createMeet("meetName1");
        Meet meet2 = createMeet("meetName2");
        Meet meet3 = createMeet("meetName3");
        meetRepository.saveAll(List.of(meet1, meet2, meet3));

        MeetUser meetUser1 = createMeetUser(meet1, user, MeetUserRole.HOST);
        MeetUser meetUser2 = createMeetUser(meet2, user, MeetUserRole.PARTICIPANT);
        meetUserRepository.saveAll(List.of(meetUser1, meetUser2));

        // when
        List<Meet> meets = meetRepository.findParticipatedMeets(user.getId());

        // then
        assertThat(meets).hasSize(2)
            .extracting("meetName", "code")
            .containsExactlyInAnyOrder(
                tuple("meetName1", meet1.getCode()),
                tuple("meetName2", meet2.getCode())
            );
    }

    private Meet createMeet(String meetName) {
        return Meet.builder()
            .meetName(meetName)
            .description("description")
            .build();
    }

    private User createUser(String email) {
        return User.builder()
            .email(email)
            .password(passwordEncoder.encode("password"))
            .nickname("nickname")
            .providerType(OauthProvider.LOCAL)
            .build();
    }

    private MeetUser createMeetUser(Meet meet, User user, MeetUserRole role) {
        return MeetUser.builder()
            .meet(meet)
            .user(user)
            .meetUserRole(role)
            .build();
    }
}