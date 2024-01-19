package com.nimble.server_spring.modules.meet;

import com.nimble.server_spring.infra.error.ErrorCode;
import com.nimble.server_spring.infra.error.ErrorCodeException;
import com.nimble.server_spring.modules.meet.dto.request.MeetCreateRequestDto;
import com.nimble.server_spring.modules.meet.dto.request.MeetInviteRequestDto;
import com.nimble.server_spring.modules.user.User;
import com.nimble.server_spring.modules.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
            .host(user)
            .build();

        MeetMember meetMember = MeetMember.builder()
            .meet(meet)
            .user(user)
            .memberRole(MemberRole.HOST)
            .build();
        meet.addMeetMember(meetMember);

        return meetRepository.save(meet);
    }

    public MeetMember invite(
        User currentUser, Long meetId,
        MeetInviteRequestDto meetInviteRequestDto
    ) {
        Meet meet = meetRepository.findById(meetId)
            .orElseThrow(() -> new ErrorCodeException(ErrorCode.MEET_NOT_FOUND));

        if (!meet.isHost(currentUser.getId())) {
            throw new ErrorCodeException(ErrorCode.MEET_NOT_FOUND);
        }

        if (meet.getMeetMembers().size() >= INVITE_LIMIT_NUMBER) {
            throw new ErrorCodeException(ErrorCode.MEET_INVITE_LIMIT_OVER);
        }

        String email = meetInviteRequestDto.getEmail();
        User userToInvite = userRepository.findOneByEmail(email)
            .orElseThrow(() -> new ErrorCodeException(ErrorCode.USER_NOT_FOUND_BY_EMAIL));

        boolean isUserInvited = meet.getMeetMembers().stream()
            .anyMatch(meetToMember -> meetToMember.getUser().getEmail().equals(email));
        if (isUserInvited) {
            throw new ErrorCodeException(ErrorCode.USER_ALREADY_INVITED);
        }

        MeetMember meetMember = MeetMember.builder()
            .meet(meet)
            .user(userToInvite)
            .memberRole(MemberRole.MEMBER)
            .build();
        meet.addMeetMember(meetMember);

        return meetMember;
    }

    public MeetMember kickOut(User currentUser, Long meetId, Long memberId) {
        Meet meet = meetRepository.findById(meetId)
            .orElseThrow(() -> new ErrorCodeException(ErrorCode.MEET_NOT_FOUND));

        if (!meet.isHost(currentUser.getId())) {
            throw new ErrorCodeException(ErrorCode.MEET_NOT_FOUND);
        }

        MeetMember meetMember = meet.findMember(memberId)
            .orElseThrow(() -> new ErrorCodeException(ErrorCode.MEMBER_NOT_FOUND));
        meet.getMeetMembers().remove(meetMember);

        return meetMember;
    }
}
