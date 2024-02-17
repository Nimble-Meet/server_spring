package com.nimble.server_spring.modules.meet;

import com.nimble.server_spring.infra.error.ErrorCode;
import com.nimble.server_spring.infra.error.ErrorCodeException;
import com.nimble.server_spring.modules.meet.dto.request.MeetCreateServiceRequest;
import com.nimble.server_spring.modules.meet.dto.request.MeetInviteServiceRequest;
import com.nimble.server_spring.modules.user.User;
import com.nimble.server_spring.modules.user.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
@Validated
public class MeetService {

    private final MeetRepository meetRepository;
    private final UserRepository userRepository;

    private static final int INVITE_LIMIT_NUMBER = 3;

    public Meet createMeet(User user, @Valid MeetCreateServiceRequest meetCreateRequest) {
        Meet meet = Meet.builder()
            .meetName(meetCreateRequest.getMeetName())
            .description(meetCreateRequest.getDescription())
            .build();

        MeetUser meetUser = MeetUser.builder()
            .meet(meet)
            .user(user)
            .meetUserRole(MeetUserRole.HOST)
            .build();
        meet.addMeetUser(meetUser);

        log.info("Meet created: {}", meet);

        return meetRepository.save(meet);
    }

    public MeetUser invite(
        User currentUser,
        Long meetId,
        @Valid MeetInviteServiceRequest meetInviteRequest
    ) {
        Meet meet = meetRepository.findMeetById(meetId)
            .orElseThrow(() -> new ErrorCodeException(ErrorCode.MEET_NOT_FOUND));

        if (!meet.isHostedBy(currentUser)) {
            throw new ErrorCodeException(ErrorCode.NOT_MEET_HOST_FORBIDDEN);
        }

        if (meet.getMeetUsers().size() >= INVITE_LIMIT_NUMBER) {
            throw new ErrorCodeException(ErrorCode.MEET_INVITE_LIMIT_OVER);
        }

        String email = meetInviteRequest.getEmail();
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
