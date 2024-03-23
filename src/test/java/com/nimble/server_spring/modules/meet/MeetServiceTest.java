package com.nimble.server_spring.modules.meet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import com.nimble.server_spring.IntegrationTestSupport;
import com.nimble.server_spring.infra.error.ErrorCode;
import com.nimble.server_spring.infra.error.ErrorCodeException;
import com.nimble.server_spring.modules.auth.enums.OauthProvider;
import com.nimble.server_spring.modules.meet.dto.request.CreateMeetServiceRequest;
import com.nimble.server_spring.modules.meet.dto.request.GetMeetListServiceRequest;
import com.nimble.server_spring.modules.meet.dto.request.GetMeetServiceRequest;
import com.nimble.server_spring.modules.meet.dto.request.InviteMeetServiceRequest;
import com.nimble.server_spring.modules.meet.dto.request.KickOutMeetServiceRequest;
import com.nimble.server_spring.modules.meet.dto.response.MeetResponse;
import com.nimble.server_spring.modules.user.User;
import com.nimble.server_spring.modules.user.UserRepository;
import java.util.List;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class MeetServiceTest extends IntegrationTestSupport {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MeetRepository meetRepository;

    @Autowired
    private MeetUserRepository meetUserRepository;

    @Autowired
    private MeetService meetService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @DisplayName("미팅 이름 및 설명, 호스트 유저 정보를 받아 미팅을 생성한다.")
    @Test
    void createMeetTest() {
        // given
        User user = createUser("user@email.com");
        userRepository.save(user);

        CreateMeetServiceRequest createMeetRequest = CreateMeetServiceRequest.create(
            "my_meet",
            "example_description",
            user
        );

        // when
        MeetResponse meetResponse = meetService.createMeet(createMeetRequest);

        // then
        assertThat(meetResponse.getId()).isNotNull();
        assertThat(meetResponse)
            .extracting("meetName", "description")
            .containsExactly("my_meet", "example_description");
        assertThat(meetResponse.getMeetUsers()).hasSize(1)
            .extracting("email")
            .containsExactly("user@email.com");
    }

    @DisplayName("사용자가 참여한 미팅 목록을 조회한다.")
    @Test
    void getMeetList() {
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

        GetMeetListServiceRequest getMeetListRequest = GetMeetListServiceRequest.create(user);

        // when
        List<MeetResponse> meetResponses = meetService.getMeetList(getMeetListRequest);

        // then
        assertThat(meetResponses).hasSize(2)
            .extracting("id", "meetName")
            .containsExactlyInAnyOrder(
                AssertionsForClassTypes.tuple(meet1.getId(), "meetName1"),
                AssertionsForClassTypes.tuple(meet2.getId(), "meetName2")
            );
    }

    @DisplayName("ID에 해당하는 미팅을 조회한다.")
    @Test
    void getMeet() {
        // given
        User user = createUser("user@email.com");
        userRepository.save(user);

        Meet meet = createMeet("test_meet");
        MeetUser meetUser1 = createMeetUser(meet, user, MeetUserRole.HOST);
        meet.getMeetUsers().add(meetUser1);
        meetRepository.save(meet);

        GetMeetServiceRequest getMeetRequest = GetMeetServiceRequest.create(meet.getId(), user);

        // when
        MeetResponse meetResponse = meetService.getMeet(getMeetRequest);

        // then
        assertThat(meetResponse)
            .extracting("id", "meetName")
            .containsExactly(meet.getId(), "test_meet");
        assertThat(meetResponse.getMeetUsers()).hasSize(1)
            .extracting("email")
            .containsExactly("user@email.com");
    }

    @DisplayName("존재하지 않는 ID로 미팅을 조회하면 예외가 발생한다.")
    @Test
    void getMeetWithUnexistingId() {
        // given
        User user = createUser("user@email.com");
        userRepository.save(user);

        Meet meet = createMeet("test_meet");
        meetRepository.save(meet);

        MeetUser meetUser1 = createMeetUser(meet, user, MeetUserRole.HOST);
        meetUserRepository.save(meetUser1);
        meet.getMeetUsers().add(meetUser1);

        GetMeetServiceRequest getMeetRequest = GetMeetServiceRequest.create(999L, user);

        // when
        // then
        assertThatThrownBy(() -> meetService.getMeet(getMeetRequest))
            .isInstanceOf(ErrorCodeException.class)
            .hasMessage(ErrorCode.MEET_NOT_FOUND.getMessage());
    }

    @DisplayName("미팅에 참여하지 않은 사용자가 미팅을 조회하면 예외가 발생한다.")
    @Test
    void getMeetByNotParticipatedUser() {
        // given
        User user = createUser("user@email.com");
        userRepository.save(user);

        Meet meet = createMeet("test_meet");
        meetRepository.save(meet);

        GetMeetServiceRequest getMeetRequest = GetMeetServiceRequest.create(meet.getId(), user);

        // when
        // then
        assertThatThrownBy(() -> meetService.getMeet(getMeetRequest))
            .isInstanceOf(ErrorCodeException.class)
            .hasMessage(ErrorCode.NOT_MEET_USER_FORBIDDEN.getMessage());
    }

    @DisplayName("email에 해당하는 사용자를 미팅에 참여시킨다.")
    @Test
    void invite() {
        // given
        User host = createUser("host@email.com");
        User user = createUser("user@email.com");
        userRepository.saveAll(List.of(host, user));

        Meet meet = createMeet("test_meet");
        MeetUser meetUser = createMeetUser(meet, host, MeetUserRole.HOST);
        meet.getMeetUsers().add(meetUser);
        meetRepository.save(meet);

        InviteMeetServiceRequest inviteMeetRequest
            = InviteMeetServiceRequest.create("user@email.com", meet.getId(), host);

        // when
        MeetResponse meetResponse = meetService.invite(inviteMeetRequest);

        // then
        assertThat(meetResponse.getMeetUsers()).hasSize(2)
            .extracting("email", "role")
            .containsExactlyInAnyOrder(
                tuple("host@email.com", MeetUserRole.HOST.name()),
                tuple("user@email.com", MeetUserRole.PARTICIPANT.name())
            );

        List<MeetUser> meetUsers = meetUserRepository.findAll();
        assertThat(meetUsers).hasSize(2)
            .extracting("meet", "user", "meetUserRole")
            .containsExactlyInAnyOrder(
                tuple(meet, host, MeetUserRole.HOST),
                tuple(meet, user, MeetUserRole.PARTICIPANT)
            );
    }

    @DisplayName("존재하지 않는 ID의 미팅으로 사용자를 초대시키면 예외가 발생한다.")
    @Test
    void inviteWithNotExistingMeetId() {
        // given
        User host = createUser("host@email.com");
        User user = createUser("user@email.com");
        userRepository.saveAll(List.of(host, user));

        Meet meet = createMeet("test_meet");
        MeetUser meetUser = createMeetUser(meet, host, MeetUserRole.HOST);
        meet.getMeetUsers().add(meetUser);
        meetRepository.save(meet);

        InviteMeetServiceRequest inviteMeetRequest
            = InviteMeetServiceRequest.create("user@email.com", 999L, host);

        // when
        // then
        assertThatThrownBy(() -> meetService.invite(inviteMeetRequest))
            .isInstanceOf(ErrorCodeException.class)
            .hasMessage(ErrorCode.MEET_NOT_FOUND.getMessage());
    }

    @DisplayName("호스트가 아닌 사용자가 미팅에 사용자를 참여시키면 예외가 발생한다.")
    @Test
    void inviteByNotHostUser() {
        // given
        User user1 = createUser("user1@email.com");
        User user2 = createUser("user2@email.com");
        userRepository.saveAll(List.of(user1, user2));

        Meet meet = createMeet("test_meet");
        MeetUser meetUser = createMeetUser(meet, user1, MeetUserRole.PARTICIPANT);
        meet.getMeetUsers().add(meetUser);
        meetRepository.save(meet);

        InviteMeetServiceRequest inviteMeetRequest
            = InviteMeetServiceRequest.create("user2@email.com", meet.getId(), user1);

        // when
        // then
        assertThatThrownBy(() -> meetService.invite(inviteMeetRequest))
            .isInstanceOf(ErrorCodeException.class)
            .hasMessage(ErrorCode.NOT_MEET_HOST_FORBIDDEN.getMessage());
    }

    @DisplayName("미팅의 최대 참여자를 초과하여 사용자를 참여시키면 예외가 발생한다.")
    @Test
    void inviteOverLimit() {
        // given
        User host = createUser("host@email.com");
        User participant1 = createUser("participant1@email.com");
        User participant2 = createUser("participant2@email.com");
        User user = createUser("user@email.com");
        userRepository.saveAll(List.of(host, participant1, participant2, user));

        Meet meet = createMeet("test_meet");
        MeetUser meetUser1 = createMeetUser(meet, host, MeetUserRole.HOST);
        MeetUser meetUser2 = createMeetUser(meet, participant1, MeetUserRole.PARTICIPANT);
        MeetUser meetUser3 = createMeetUser(meet, participant2, MeetUserRole.PARTICIPANT);
        meet.getMeetUsers().addAll(List.of(meetUser1, meetUser2, meetUser3));
        meetRepository.save(meet);

        InviteMeetServiceRequest inviteMeetRequest
            = InviteMeetServiceRequest.create("user@email.com", meet.getId(), host);

        // when
        // then
        assertThatThrownBy(() -> meetService.invite(inviteMeetRequest))
            .isInstanceOf(ErrorCodeException.class)
            .hasMessage(ErrorCode.MEET_INVITE_LIMIT_OVER.getMessage());
    }

    @DisplayName("존재하지 않는 이메일로 사용자를 참여시키면 예외가 발생한다.")
    @Test
    void inviteWithNotExistingEmail() {
        // given
        User host = createUser("host@email.com");
        User user = createUser("user@email.com");
        userRepository.saveAll(List.of(host, user));

        Meet meet = createMeet("test_meet");
        MeetUser meetUser = createMeetUser(meet, host, MeetUserRole.HOST);
        meet.getMeetUsers().add(meetUser);
        meetRepository.save(meet);

        InviteMeetServiceRequest inviteMeetRequest
            = InviteMeetServiceRequest.create("UNKNOWN@email.com", meet.getId(), host);

        // when
        // then
        assertThatThrownBy(() -> meetService.invite(inviteMeetRequest))
            .isInstanceOf(ErrorCodeException.class)
            .hasMessage(ErrorCode.USER_NOT_FOUND_BY_EMAIL.getMessage());
    }

    @DisplayName("이미 미팅에 참여한 사용자를 참여시키면 예외가 발생한다.")
    @Test
    void inviteAlreadyParticipatedUser() {
        // given
        User host = createUser("host@email.com");
        User user = createUser("user@email.com");
        userRepository.saveAll(List.of(host, user));

        Meet meet = createMeet("test_meet");
        MeetUser meetUser1 = createMeetUser(meet, host, MeetUserRole.HOST);
        MeetUser meetUser2 = createMeetUser(meet, user, MeetUserRole.PARTICIPANT);
        meet.getMeetUsers().addAll(List.of(meetUser1, meetUser2));
        meetRepository.save(meet);

        InviteMeetServiceRequest inviteMeetRequest
            = InviteMeetServiceRequest.create("user@email.com", meet.getId(), host);

        // when
        // then
        assertThatThrownBy(() -> meetService.invite(inviteMeetRequest))
            .isInstanceOf(ErrorCodeException.class)
            .hasMessage(ErrorCode.USER_ALREADY_INVITED.getMessage());
    }

    @DisplayName("이메일에 해당하는 사용자를 미팅에서 강퇴시킨다.")
    @Test
    void kickOut() {
        // given
        User host = createUser("host@email.com");
        User user = createUser("user@email.com");
        userRepository.saveAll(List.of(host, user));

        Meet meet = createMeet("test_meet");
        MeetUser meetUser1 = createMeetUser(meet, host, MeetUserRole.HOST);
        MeetUser meetUser2 = createMeetUser(meet, user, MeetUserRole.PARTICIPANT);
        meet.getMeetUsers().addAll(List.of(meetUser1, meetUser2));
        meetRepository.save(meet);

        KickOutMeetServiceRequest kickOutMeetRequest =
            KickOutMeetServiceRequest.create("user@email.com", meet.getId(), host);

        // when
        MeetResponse meetResponse = meetService.kickOut(kickOutMeetRequest);

        // then
        assertThat(meetResponse.getMeetUsers()).hasSize(1)
            .extracting("email", "role")
            .containsExactlyInAnyOrder(tuple("host@email.com", MeetUserRole.HOST.name()));

        List<MeetUser> meetUsers = meetUserRepository.findAll();
        assertThat(meetUsers).hasSize(1)
            .extracting("meet", "user", "meetUserRole")
            .containsExactlyInAnyOrder(tuple(meet, host, MeetUserRole.HOST));
    }

    @DisplayName("존재하지 않는 ID의 미팅에서 사용자를 강퇴하면 예외가 발생한다.")
    @Test
    void kickOutWithNotExistingMeetId() {
        // given
        User host = createUser("host@email.com");
        User user = createUser("user@email.com");
        userRepository.saveAll(List.of(host, user));

        Meet meet = createMeet("test_meet");
        MeetUser meetUser1 = createMeetUser(meet, host, MeetUserRole.HOST);
        MeetUser meetUser2 = createMeetUser(meet, user, MeetUserRole.PARTICIPANT);
        meet.getMeetUsers().addAll(List.of(meetUser1, meetUser2));
        meetRepository.save(meet);

        KickOutMeetServiceRequest kickOutMeetRequest =
            KickOutMeetServiceRequest.create("user@email.com", 999L, host);

        // when
        // then
        assertThatThrownBy(() -> meetService.kickOut(kickOutMeetRequest))
            .isInstanceOf(ErrorCodeException.class)
            .hasMessage(ErrorCode.MEET_NOT_FOUND.getMessage());
    }

    @DisplayName("호스트가 아닌 사용자가 미팅에서 사용자를 강퇴하면 예외가 발생한다.")
    @Test
    void kickOutByNotHostUser() {
        // given
        User user1 = createUser("user1@email.com");
        User user2 = createUser("user2@email.com");
        userRepository.saveAll(List.of(user1, user2));

        Meet meet = createMeet("test_meet");
        MeetUser meetUser1 = createMeetUser(meet, user1, MeetUserRole.PARTICIPANT);
        MeetUser meetUser2 = createMeetUser(meet, user2, MeetUserRole.PARTICIPANT);
        meet.getMeetUsers().addAll(List.of(meetUser1, meetUser2));
        meetRepository.save(meet);

        KickOutMeetServiceRequest kickOutMeetRequest =
            KickOutMeetServiceRequest.create("user2@email.com", meet.getId(), user1);

        // when
        // then
        assertThatThrownBy(() -> meetService.kickOut(kickOutMeetRequest))
            .isInstanceOf(ErrorCodeException.class)
            .hasMessage(ErrorCode.NOT_MEET_HOST_FORBIDDEN.getMessage());
    }

    @DisplayName("존재하지 않는 이메일로 사용자를 참여시키면 예외가 발생한다.")
    @Test
    void kickOutWithNotExistingEmail() {
        // given
        User host = createUser("host@email.com");
        userRepository.save(host);

        Meet meet = createMeet("test_meet");
        MeetUser meetUser = createMeetUser(meet, host, MeetUserRole.HOST);
        meet.getMeetUsers().add(meetUser);
        meetRepository.save(meet);

        KickOutMeetServiceRequest kickOutMeetRequest
            = KickOutMeetServiceRequest.create("UNKNOWN@email.com", meet.getId(), host);

        // when
        // then
        assertThatThrownBy(() -> meetService.kickOut(kickOutMeetRequest))
            .isInstanceOf(ErrorCodeException.class)
            .hasMessage(ErrorCode.USER_NOT_FOUND_BY_EMAIL.getMessage());
    }

    @DisplayName("미팅에 참여하지 않은 사용자를 강퇴하면 예외가 발생한다.")
    @Test
    void kickOutNotParticipatedUser() {
        // given
        User host = createUser("host@email.com");
        User user = createUser("user@email.com");
        userRepository.saveAll(List.of(host, user));

        Meet meet = createMeet("test_meet");
        MeetUser meetUser = createMeetUser(meet, host, MeetUserRole.HOST);
        meet.getMeetUsers().add(meetUser);
        meetRepository.save(meet);

        KickOutMeetServiceRequest kickOutMeetRequest
            = KickOutMeetServiceRequest.create("user@email.com", meet.getId(), host);

        // when
        // then
        assertThatThrownBy(() -> meetService.kickOut(kickOutMeetRequest))
            .isInstanceOf(ErrorCodeException.class)
            .hasMessage(ErrorCode.USER_NOT_INVITED.getMessage());
    }

    @DisplayName("호스트인 사용자를 강퇴하면 예외가 발생한다.")
    @Test
    void kickOutHostUser() {
        // given
        User host = createUser("host@email.com");
        userRepository.save(host);

        Meet meet = createMeet("test_meet");
        MeetUser meetUser = createMeetUser(meet, host, MeetUserRole.HOST);
        meet.getMeetUsers().add(meetUser);
        meetRepository.save(meet);

        KickOutMeetServiceRequest kickOutMeetRequest
            = KickOutMeetServiceRequest.create("host@email.com", meet.getId(), host);

        // when
        // then
        assertThatThrownBy(() -> meetService.kickOut(kickOutMeetRequest))
            .isInstanceOf(ErrorCodeException.class)
            .hasMessage(ErrorCode.CANNOT_KICKOUT_HOST.getMessage());
    }

    private User createUser(String email) {
        return User.builder()
            .email(email)
            .password(passwordEncoder.encode("password"))
            .nickname("nickname")
            .providerType(OauthProvider.LOCAL)
            .build();
    }

    private Meet createMeet(String meetName) {
        return Meet.builder()
            .meetName(meetName)
            .description("description")
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