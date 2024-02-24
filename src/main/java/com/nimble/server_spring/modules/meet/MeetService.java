package com.nimble.server_spring.modules.meet;

import com.nimble.server_spring.infra.error.ErrorCode;
import com.nimble.server_spring.infra.error.ErrorCodeException;
import com.nimble.server_spring.modules.meet.dto.request.CreateMeetServiceRequest;
import com.nimble.server_spring.modules.meet.dto.request.GetMeetListServiceRequest;
import com.nimble.server_spring.modules.meet.dto.request.GetMeetServiceRequest;
import com.nimble.server_spring.modules.meet.dto.request.InviteMeetServiceRequest;
import com.nimble.server_spring.modules.meet.dto.request.KickOutMeetServiceRequest;
import com.nimble.server_spring.modules.meet.dto.response.MeetResponse;
import com.nimble.server_spring.modules.user.User;
import com.nimble.server_spring.modules.user.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
@Slf4j
@Validated
public class MeetService {

    private final MeetRepository meetRepository;
    private final UserRepository userRepository;
    private final EntityManager entityManager;

    private static final int INVITE_LIMIT_NUMBER = 3;

    @Transactional
    public MeetResponse createMeet(
        @Valid CreateMeetServiceRequest meetCreateRequest
    ) {
        Meet meet = meetCreateRequest.toEntity();
        return MeetResponse.fromMeet(meetRepository.save(meet));
    }

    public List<MeetResponse> getMeetList(GetMeetListServiceRequest getMeetListRequest) {
        return meetRepository.findParticipatedMeets(getMeetListRequest.getUser().getId())
            .stream()
            .map(MeetResponse::fromMeet)
            .toList();
    }

    public MeetResponse getMeet(GetMeetServiceRequest getMeetRequest) {
        Meet meet = meetRepository.findMeetById(getMeetRequest.getMeetId())
            .orElseThrow(() -> new ErrorCodeException(ErrorCode.MEET_NOT_FOUND));

        if (!meet.isParticipatedBy(getMeetRequest.getUser())) {
            throw new ErrorCodeException(ErrorCode.NOT_MEET_USER_FORBIDDEN);
        }
        return MeetResponse.fromMeet(meet);
    }

    @Transactional
    public MeetResponse invite(
        @Valid InviteMeetServiceRequest inviteMeetRequest
    ) {
        Meet meet = meetRepository.findMeetById(inviteMeetRequest.getMeetId())
            .orElseThrow(() -> new ErrorCodeException(ErrorCode.MEET_NOT_FOUND));
        if (!meet.isHostedBy(inviteMeetRequest.getCurrentUser())) {
            throw new ErrorCodeException(ErrorCode.NOT_MEET_HOST_FORBIDDEN);
        }

        if (meet.getMeetUsers().size() >= INVITE_LIMIT_NUMBER) {
            throw new ErrorCodeException(ErrorCode.MEET_INVITE_LIMIT_OVER);
        }

        User userToInvite = userRepository.findOneByEmail(inviteMeetRequest.getEmail())
            .orElseThrow(() -> new ErrorCodeException(ErrorCode.USER_NOT_FOUND_BY_EMAIL));
        if (meet.isParticipatedBy(userToInvite)) {
            throw new ErrorCodeException(ErrorCode.USER_ALREADY_INVITED);
        }

        meet.addParticipant(userToInvite);
        entityManager.flush();
        return MeetResponse.fromMeet(meet);
    }

    @Transactional
    public MeetResponse kickOut(KickOutMeetServiceRequest kickOutMeetRequest) {
        Meet meet = meetRepository.findMeetById(kickOutMeetRequest.getMeetId())
            .orElseThrow(() -> new ErrorCodeException(ErrorCode.MEET_NOT_FOUND));
        if (!meet.isHostedBy(kickOutMeetRequest.getCurrentUser())) {
            throw new ErrorCodeException(ErrorCode.NOT_MEET_HOST_FORBIDDEN);
        }

        User userToKickOut = userRepository.findOneByEmail(kickOutMeetRequest.getEmail())
            .orElseThrow(() -> new ErrorCodeException(ErrorCode.USER_NOT_FOUND_BY_EMAIL));
        if (!meet.isParticipatedBy(userToKickOut)) {
            throw new ErrorCodeException(ErrorCode.USER_NOT_INVITED);
        }
        if (meet.isHostedBy(userToKickOut)) {
            throw new ErrorCodeException(ErrorCode.CANNOT_KICKOUT_HOST);
        }

        meet.removeParticipant(userToKickOut);
        return MeetResponse.fromMeet(meet);
    }
}
