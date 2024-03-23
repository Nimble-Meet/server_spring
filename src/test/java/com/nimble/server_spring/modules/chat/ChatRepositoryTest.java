package com.nimble.server_spring.modules.chat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

import com.nimble.server_spring.IntegrationTestSupport;
import com.nimble.server_spring.modules.auth.enums.OauthProvider;
import com.nimble.server_spring.modules.chat.dto.response.ChatResponse;
import com.nimble.server_spring.modules.meet.Meet;
import com.nimble.server_spring.modules.meet.MeetRepository;
import com.nimble.server_spring.modules.meet.MeetUser;
import com.nimble.server_spring.modules.meet.MeetUserRepository;
import com.nimble.server_spring.modules.meet.MeetUserRole;
import com.nimble.server_spring.modules.user.User;
import com.nimble.server_spring.modules.user.UserRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class ChatRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MeetRepository meetRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @DisplayName("ID에 해당하는 미팅의 채팅 목록을 조회한다.")
    @Test
    void findAllByMeetId() {
        // given
        User user = createUser("user@email.com");
        userRepository.save(user);

        Meet meet1 = createMeet("meetName1");
        Meet meet2 = createMeet("meetName2");
        MeetUser meetUser1 = createMeetUser(meet1, user, MeetUserRole.HOST);
        meet1.getMeetUsers().add(meetUser1);
        MeetUser meetUser2 = createMeetUser(meet2, user, MeetUserRole.HOST);
        meet2.getMeetUsers().add(meetUser2);
        meetRepository.saveAll(List.of(meet1, meet2));

        Chat chat1 = createChat(ChatType.ENTER, meetUser1);
        Chat chat2 = createChat(ChatType.LEAVE, meetUser1);
        Chat chat3 = createChat(ChatType.ENTER, meetUser2);
        chatRepository.saveAll(List.of(chat1, chat2, chat3));

        // when
        Slice<ChatResponse> chatResponses = chatRepository.findAllByMeetId(
            meet1.getId(),
            PageRequest.ofSize(10)
        );

        // then
        assertThat(chatResponses.getContent()).hasSize(2)
            .extracting("email", "meetId", "chatType")
            .containsExactlyInAnyOrder(
                tuple("user@email.com", meet1.getId(), ChatType.ENTER),
                tuple("user@email.com", meet1.getId(), ChatType.LEAVE)
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

    private static Chat createChat(ChatType chatType, MeetUser meetUser) {
        return Chat.builder()
            .chatType(chatType)
            .meetUser(meetUser)
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