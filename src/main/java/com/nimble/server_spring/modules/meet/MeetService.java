package com.nimble.server_spring.modules.meet;

import com.nimble.server_spring.infra.error.ErrorCode;
import com.nimble.server_spring.infra.error.ErrorCodeException;
import com.nimble.server_spring.modules.meet.dto.request.MeetCreateServiceRequest;
import com.nimble.server_spring.modules.meet.dto.request.MeetInviteServiceRequest;
import com.nimble.server_spring.modules.meet.dto.response.MeetResponse;
import com.nimble.server_spring.modules.user.User;
import com.nimble.server_spring.modules.user.UserRepository;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final MeetUserRepository meetUserRepository;

    private static final int INVITE_LIMIT_NUMBER = 3;

    public MeetResponse createMeet(
        User user, @Valid MeetCreateServiceRequest meetCreateRequest
    ) {
        Meet meet = Meet.builder()
            .meetName(meetCreateRequest.getMeetName())
            .description(meetCreateRequest.getDescription())
            .build();
        meet.addUser(user, MeetUserRole.HOST);
        return MeetResponse.fromMeet(meetRepository.save(meet));
    }

    public List<MeetResponse> getMeetList(User currentUser) {
        return meetRepository.findParticipatedMeets(currentUser.getId())
            .stream()
            .map(MeetResponse::fromMeet)
            .toList();
    }

    public MeetResponse getMeet(Long meetId, User currentUser) {
        Meet meet = meetRepository.findMeetById(meetId)
            .orElseThrow(() -> new ErrorCodeException(ErrorCode.MEET_NOT_FOUND));

        if (!meet.isParticipatedBy(currentUser)) {
            throw new ErrorCodeException(ErrorCode.NOT_MEET_USER_FORBIDDEN);
        }
        return MeetResponse.fromMeet(meet);
    }

    public MeetResponse invite(
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

        User userToInvite = userRepository.findOneByEmail(meetInviteRequest.getEmail())
            .orElseThrow(() -> new ErrorCodeException(ErrorCode.USER_NOT_FOUND_BY_EMAIL));
        if (meet.isParticipatedBy(userToInvite)) {
            throw new ErrorCodeException(ErrorCode.USER_ALREADY_INVITED);
        }
        meet.addUser(userToInvite, MeetUserRole.MEMBER);
        return MeetResponse.fromMeet(meet);
    }

    public MeetResponse kickOut(User currentUser, Long meetId, Long meetUserId) {
        Meet meet = meetRepository.findMeetById(meetId)
            .orElseThrow(() -> new ErrorCodeException(ErrorCode.MEET_NOT_FOUND));

        if (!meet.isHostedBy(currentUser)) {
            throw new ErrorCodeException(ErrorCode.NOT_MEET_HOST_FORBIDDEN);
        }

        MeetUser meetUser = meetUserRepository.findById(meetUserId)
            .orElseThrow(() -> new ErrorCodeException(ErrorCode.MEET_USER_NOT_FOUND));
        if (!meet.hasMeetUser(meetUser)) {
            throw new ErrorCodeException(ErrorCode.MEET_USER_NOT_FOUND);
        }
        meet.removeMeetUser(meetUser);
        return MeetResponse.fromMeet(meet);
    }
}
