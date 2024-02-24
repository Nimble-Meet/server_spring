package com.nimble.server_spring.modules.chat;

import com.nimble.server_spring.infra.error.ErrorCode;
import com.nimble.server_spring.infra.error.ErrorCodeException;
import com.nimble.server_spring.modules.chat.dto.request.EnterChatServiceRequest;
import com.nimble.server_spring.modules.chat.dto.request.LeaveChatServiceRequest;
import com.nimble.server_spring.modules.chat.dto.request.TalkChatServiceRequest;
import com.nimble.server_spring.modules.chat.dto.request.GetChatListServiceRequest;
import com.nimble.server_spring.modules.chat.dto.response.ChatResponse;
import com.nimble.server_spring.modules.meet.MeetUser;
import com.nimble.server_spring.modules.meet.MeetUserRepository;
import com.nimble.server_spring.modules.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {

    private final ChatRepository chatRepository;
    private final MeetUserRepository meetUserRepository;

    @Transactional
    public ChatResponse enterChat(EnterChatServiceRequest enterChatRequest) {
        MeetUser meetUser = meetUserRepository
            .findByUserIdAndMeetId(
                enterChatRequest.getCurrentUser().getId(),
                enterChatRequest.getMeetId()
            )
            .orElseThrow(() -> new ErrorCodeException(ErrorCode.NOT_MEET_USER_FORBIDDEN));
        meetUser.enterMeet();

        Chat chat = chatRepository.save(Chat.createEnter(meetUser));
        return ChatResponse.fromChat(chat);
    }

    @Transactional
    public ChatResponse talkChat(TalkChatServiceRequest talkChatServiceRequest) {
        MeetUser meetUser = meetUserRepository
            .findByUserIdAndMeetId(
                talkChatServiceRequest.getCurrentUser().getId(),
                talkChatServiceRequest.getMeetId()
            )
            .orElseThrow(() -> new ErrorCodeException(ErrorCode.NOT_MEET_USER_FORBIDDEN));

        Chat chat = chatRepository.save(
            Chat.createTalk(meetUser, talkChatServiceRequest.getMessage())
        );
        return ChatResponse.fromChat(chat);
    }

    @Transactional
    public ChatResponse leaveChat(LeaveChatServiceRequest leaveChatRequest) {
        MeetUser meetUser = meetUserRepository.findMeetUserById(leaveChatRequest.getMeetUserId())
            .orElseThrow(() -> new ErrorCodeException(ErrorCode.NOT_MEET_USER_FORBIDDEN));
        meetUser.leaveMeet();

        Chat chat = chatRepository.save(Chat.createLeave(meetUser));
        return ChatResponse.fromChat(chat);
    }

    public Slice<ChatResponse> getChatList(GetChatListServiceRequest getChatListRequest) {
        if (!isParticipated(getChatListRequest.getCurrentUser(), getChatListRequest.getMeetId())) {
            throw new ErrorCodeException(ErrorCode.NOT_MEET_USER_FORBIDDEN);
        }

        return chatRepository.findAllByMeetId(
            getChatListRequest.getMeetId(),
            PageRequest.of(
                getChatListRequest.getPage(),
                getChatListRequest.getSize(),
                Sort.by(Direction.DESC, "createdAt")
            )
        );
    }

    private boolean isParticipated(User currentUser, Long meetId) {
        return meetUserRepository.existsByUser_IdAndMeet_Id(
            currentUser.getId(),
            meetId
        );
    }
}
