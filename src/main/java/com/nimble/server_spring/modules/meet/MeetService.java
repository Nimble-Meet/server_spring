package com.nimble.server_spring.modules.meet;

import com.nimble.server_spring.infra.error.ErrorCode;
import com.nimble.server_spring.infra.error.ErrorCodeException;
import com.nimble.server_spring.modules.meet.dto.request.MeetCreateRequestDto;
import com.nimble.server_spring.modules.meet.dto.request.MeetInviteRequestDto;
import com.nimble.server_spring.modules.user.User;
import com.nimble.server_spring.modules.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class MeetService {

    private final MeetRepository meetRepository;
    private final UserRepository userRepository;

    private static final int INVITE_LIMIT_NUMBER = 3;

    public Meet createMeet(User user, MeetCreateRequestDto meetCreateRequestDto) {
        Meet meet = Meet.builder()
            .meetName(meetCreateRequestDto.getMeetName())
            .description(meetCreateRequestDto.getDescription())
            .build();

        MeetUser meetUser = MeetUser.builder()
            .meet(meet)
            .user(user)
            .meetUserRole(MeetUserRole.HOST)
            .build();
        meet.addMeetUser(meetUser);

        return meetRepository.save(meet);
    }

    public MeetUser invite(
        User currentUser,
        Long meetId,
        MeetInviteRequestDto meetInviteRequestDto
    ) {
        Meet meet = meetRepository.findMeetById(meetId)
            .orElseThrow(() -> new ErrorCodeException(ErrorCode.MEET_NOT_FOUND));

        if (!meet.isHostedBy(currentUser)) {
            throw new ErrorCodeException(ErrorCode.NOT_MEET_HOST_FORBIDDEN);
        }

        if (meet.getMeetUsers().size() >= INVITE_LIMIT_NUMBER) {
            throw new ErrorCodeException(ErrorCode.MEET_INVITE_LIMIT_OVER);
        }

        String email = meetInviteRequestDto.getEmail();
        User userToInvite = userRepository.findOneByEmail(email)
            .orElseThrow(() -> new ErrorCodeException(ErrorCode.USER_NOT_FOUND_BY_EMAIL));

        boolean isUserInvited = meet.getMeetUsers().stream()
            .anyMatch(meetUser -> meetUser.getUser().getEmail().equals(email));
        if (isUserInvited) {
            throw new ErrorCodeException(ErrorCode.USER_ALREADY_INVITED);
        }

        MeetUser meetUser = MeetUser.builder()
            .meet(meet)
            .user(userToInvite)
            .meetUserRole(MeetUserRole.MEMBER)
            .build();
        meet.addMeetUser(meetUser);

        return meetUser;
    }

    public MeetUser kickOut(User currentUser, Long meetId, Long meetUserId) {
        Meet meet = meetRepository.findMeetById(meetId)
            .orElseThrow(() -> new ErrorCodeException(ErrorCode.MEET_NOT_FOUND));

        if (!meet.isHostedBy(currentUser)) {
            throw new ErrorCodeException(ErrorCode.NOT_MEET_HOST_FORBIDDEN);
        }

        MeetUser meetUser = meet.findMeetUser(meetUserId)
            .orElseThrow(() -> new ErrorCodeException(ErrorCode.MEET_USER_NOT_FOUND));
        meet.getMeetUsers().remove(meetUser);

        return meetUser;
    }
}
