package com.nimble.server_spring.modules.chat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

import com.nimble.server_spring.IntegrationTestSupport;
import com.nimble.server_spring.infra.error.ErrorCode;
import com.nimble.server_spring.infra.error.ErrorCodeException;
import com.nimble.server_spring.modules.auth.enums.OauthProvider;
import com.nimble.server_spring.modules.chat.dto.request.EnterChatServiceRequest;
import com.nimble.server_spring.modules.chat.dto.request.GetChatListServiceRequest;
import com.nimble.server_spring.modules.chat.dto.request.LeaveChatServiceRequest;
import com.nimble.server_spring.modules.chat.dto.request.TalkChatServiceRequest;
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
import org.springframework.data.domain.Slice;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class ChatServiceTest extends IntegrationTestSupport {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MeetRepository meetRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ChatService chatService;

    @DisplayName("미팅 참여자는 미팅의 채팅에 입장할 수 있다.")
    @Test
    void enterChat() {
        // given
        User user = createUser("user@email.com");
        userRepository.save(user);

        Meet meet = createMeet("example_meet");
        MeetUser meetUser = createMeetUser(meet, user, MeetUserRole.HOST);
        meet.getMeetUsers().add(meetUser);
        meetRepository.save(meet);

        EnterChatServiceRequest enterChatRequest = EnterChatServiceRequest.create(
            user,
            meet.getId()
        );

        // when
        ChatResponse chatResponse = chatService.enterChat(enterChatRequest);

        // then
        assertThat(chatResponse)
            .extracting("email", "meetId", "chatType")
            .containsExactly("user@email.com", meet.getId(), ChatType.ENTER);

        List<Chat> chats = chatRepository.findAll();
        assertThat(chats).hasSize(1);
    }

    @DisplayName("미팅에 참여하지 않은 사용자가 채팅에 입장하면 예외가 발생한다.")
    @Test
    void enterChatByNotParticipatedUser() {
        // given
        User user = createUser("user@email.com");
        userRepository.save(user);

        Meet meet = createMeet("example_meet");
        meetRepository.save(meet);

        EnterChatServiceRequest enterChatRequest = EnterChatServiceRequest.create(
            user,
            meet.getId()
        );

        // when // then
        assertThatThrownBy(() -> chatService.enterChat(enterChatRequest))
            .isInstanceOf(ErrorCodeException.class)
            .hasMessage(ErrorCode.NOT_MEET_USER_FORBIDDEN.getMessage());
    }

    @DisplayName("미팅의 참여자는 미팅의 채팅에서 말할 수 있다.")
    @Test
    void talkChat() {
        // given
        User user = createUser("user@email.com");
        userRepository.save(user);

        Meet meet = createMeet("example_meet");
        MeetUser meetUser = createMeetUser(meet, user, MeetUserRole.HOST);
        meet.getMeetUsers().add(meetUser);
        meetRepository.save(meet);

        TalkChatServiceRequest talkChatRequest
            = TalkChatServiceRequest.create(user, meet.getId(), "test_message");

        // when
        ChatResponse chatResponse = chatService.talkChat(talkChatRequest);

        // then
        assertThat(chatResponse)
            .extracting("email", "meetId", "chatType", "message")
            .containsExactly("user@email.com", meet.getId(), ChatType.TALK, "test_message");

        List<Chat> chats = chatRepository.findAll();
        assertThat(chats).hasSize(1);
    }

    @DisplayName("미팅에 참여하지 않은 사용자가 채팅에서 말하면 예외가 발생한다.")
    @Test
    void talkChatByNotParticipatedUser() {
        // given
        User user = createUser("user@email.com");
        userRepository.save(user);

        Meet meet = createMeet("example_meet");
        meetRepository.save(meet);

        TalkChatServiceRequest talkChatRequest
            = TalkChatServiceRequest.create(user, meet.getId(), "test_message");

        // when // then
        assertThatThrownBy(() -> chatService.talkChat(talkChatRequest))
            .isInstanceOf(ErrorCodeException.class)
            .hasMessage(ErrorCode.NOT_MEET_USER_FORBIDDEN.getMessage());
    }

    @DisplayName("미팅 참여자는 미팅의 채팅에서 퇴장할 수 있다.")
    @Test
    void leaveChat() {
        // given
        User user = createUser("user@email.com");
        userRepository.save(user);

        Meet meet = createMeet("example_meet");
        MeetUser meetUser = createMeetUser(meet, user, MeetUserRole.HOST);
        meet.getMeetUsers().add(meetUser);
        meetRepository.save(meet);

        LeaveChatServiceRequest leaveChatRequest
            = LeaveChatServiceRequest.create("user@email.com", meet.getId());

        // when
        ChatResponse chatResponse = chatService.leaveChat(leaveChatRequest);

        // then
        assertThat(chatResponse)
            .extracting("email", "meetId", "chatType")
            .containsExactly("user@email.com", meet.getId(), ChatType.LEAVE);

        List<Chat> chats = chatRepository.findAll();
        assertThat(chats).hasSize(1);
    }

    @DisplayName("미팅에 참여하지 않은 사용자가 채팅에서 퇴장하면 예외가 발생한다.")
    @Test
    void leaveChatByNotParticipatedUser() {
        // given
        User user = createUser("user@email.com");
        userRepository.save(user);

        Meet meet = createMeet("example_meet");
        meetRepository.save(meet);

        LeaveChatServiceRequest leaveChatRequest
            = LeaveChatServiceRequest.create("user@email.com", meet.getId());

        // when
        assertThatThrownBy(() -> chatService.leaveChat(leaveChatRequest))
            .isInstanceOf(ErrorCodeException.class)
            .hasMessage(ErrorCode.NOT_MEET_USER_FORBIDDEN.getMessage());
    }

    @DisplayName("미팅 참여자는 미팅의 채팅 목록을 조회할 수 있다.")
    @Test
    void getChatList() {
        // given
        User user = createUser("user@email.com");
        userRepository.save(user);

        Meet meet = createMeet("meetName1");
        MeetUser meetUser = createMeetUser(meet, user, MeetUserRole.HOST);
        meet.getMeetUsers().add(meetUser);
        meetRepository.save(meet);

        Chat chat1 = createChat(ChatType.ENTER, meetUser);
        Chat chat2 = createChat(ChatType.LEAVE, meetUser);
        chatRepository.saveAll(List.of(chat1, chat2));

        GetChatListServiceRequest getChatListRequest
            = GetChatListServiceRequest.create(user, meet.getId(), 10, 0);

        // when
        Slice<ChatResponse> chatResponseSlice = chatService.getChatList(getChatListRequest);

        // then
        assertThat(chatResponseSlice.getContent()).hasSize(2)
            .extracting("email", "meetId", "chatType")
            .containsExactlyInAnyOrder(
                tuple("user@email.com", meet.getId(), ChatType.ENTER),
                tuple("user@email.com", meet.getId(), ChatType.LEAVE)
            );

        List<Chat> chats = chatRepository.findAll();
        assertThat(chats).hasSize(2);
    }

    @DisplayName("미팅에 참여하지 않은 사용자가 채팅 목록을 조회하면 예외가 발생한다.")
    @Test
    void getChatListByNotParticipatedUser() {
        // given
        User user = createUser("user@email.com");
        userRepository.save(user);

        Meet meet = createMeet("meetName1");
        meetRepository.save(meet);

        GetChatListServiceRequest getChatListRequest
            = GetChatListServiceRequest.create(user, meet.getId(), 10, 0);

        // when
        assertThatThrownBy(() -> chatService.getChatList(getChatListRequest))
            .isInstanceOf(ErrorCodeException.class)
            .hasMessage(ErrorCode.NOT_MEET_USER_FORBIDDEN.getMessage());
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