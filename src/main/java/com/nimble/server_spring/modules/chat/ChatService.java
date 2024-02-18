package com.nimble.server_spring.modules.chat;

import com.nimble.server_spring.infra.error.ErrorCode;
import com.nimble.server_spring.infra.error.ErrorCodeException;
import com.nimble.server_spring.modules.chat.dto.request.ChatTalkServiceRequest;
import com.nimble.server_spring.modules.meet.MeetUser;
import com.nimble.server_spring.modules.meet.MeetUserRepository;
import com.nimble.server_spring.modules.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatService {

    private final ChatRepository chatRepository;
    private final MeetUserRepository meetUserRepository;

    public Chat enterChat(User currentUser, Long meetId) {
        MeetUser meetUser = meetUserRepository
            .findByUserIdAndMeetId(currentUser.getId(), meetId)
            .orElseThrow(() -> new ErrorCodeException(ErrorCode.MEET_USER_NOT_FOUND));
        meetUser.enterMeet();

        return chatRepository.save(
            Chat.builder()
                .chatType(ChatType.ENTER)
                .meet(meetUser.getMeet())
                .meetUser(meetUser)
                .build()
        );
    }

    public Chat talkChat(
        User currentUser,
        Long meetId,
        ChatTalkServiceRequest chatTalkServiceRequest
    ) {
        MeetUser meetUser = meetUserRepository
            .findByUserIdAndMeetId(currentUser.getId(), meetId)
            .orElseThrow(() -> new ErrorCodeException(ErrorCode.MEET_USER_NOT_FOUND));

        return chatRepository.save(
            Chat.builder()
                .chatType(ChatType.TALK)
                .message(chatTalkServiceRequest.getMessage())
                .meet(meetUser.getMeet())
                .meetUser(meetUser)
                .build()
        );
    }

    public Chat leaveChat(Long meetUserId) {
        MeetUser meetUser = meetUserRepository.findMeetUserById(meetUserId)
            .orElseThrow(() -> new ErrorCodeException(ErrorCode.MEET_USER_NOT_FOUND));
        meetUser.leaveMeet();

        return chatRepository.save(
            Chat.builder()
                .chatType(ChatType.LEAVE)
                .meet(meetUser.getMeet())
                .meetUser(meetUser)
                .build()
        );
    }
}
