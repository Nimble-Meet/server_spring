package com.nimble.server_spring.modules.meet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

import com.nimble.server_spring.IntegrationTestSupport;
import com.nimble.server_spring.modules.meet.dto.request.CreateMeetServiceRequest;
import com.nimble.server_spring.modules.meet.dto.response.MeetResponse;
import com.nimble.server_spring.modules.user.User;
import com.nimble.server_spring.modules.user.UserRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class MeetServiceTest extends IntegrationTestSupport {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MeetRepository meetRepository;

    @Autowired
    private MeetService meetService;

    @DisplayName("미팅 이름 및 설명, 호스트 유저 정보를 받아 미팅을 생성한다.")
    @Test
    void createMeet() {
        // given
        User user = createLocalUser("user@email.com");
        userRepository.save(user);

        CreateMeetServiceRequest createMeetRequest = CreateMeetServiceRequest.create(
            "my_meet",
            "example_description",
            user
        );

        // when
        MeetResponse meetResponse = meetService.createMeet(createMeetRequest);

        // then
        assertThat(meetResponse).isNotNull()
            .extracting("meetName", "description")
            .containsExactly("my_meet", "example_description");

        List<Meet> meets = meetRepository.findAll();
        assertThat(meets).hasSize(1)
            .extracting("meetName", "description")
            .containsExactly(
                tuple("my_meet", "example_description")
            );
        assertThat(meets.get(0).getMeetUsers()).hasSize(1)
            .satisfiesExactly(
                meetUser -> {
                    assertThat(meetUser.getUser()).isEqualTo(user);
                }
            );
    }

    private static User createLocalUser(String email) {
        return User.createLocalUser(
            email,
            "$2a$12$SE54wBhXbyHzeicLdzFK5OOvuJq0mg29ScXLIeDEjeQoGLlJylDb6",
            "user1"
        );
    }
}