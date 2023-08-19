package com.nimble.server_spring.modules.meet;

import com.nimble.server_spring.modules.meet.dto.request.MeetCreateRequestDto;
import com.nimble.server_spring.modules.meet.dto.request.MeetInviteRequestDto;
import com.nimble.server_spring.modules.user.User;
import com.nimble.server_spring.modules.user.UserErrorMessages;
import com.nimble.server_spring.modules.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class MeetService {
    private final MeetRepository meetRepository;
    private final UserRepository userRepository;
    private final MeetMemberRepository meetMemberRepository;

    private static final int INVITE_LIMIT_NUMBER = 3;

    public List<Meet> getHostedOrInvitedMeets(User user) {
        return meetRepository.findHostedOrInvitedMeetsByUserId(user.getId());
    }

    public Meet createMeet(User user, MeetCreateRequestDto meetCreateRequestDto) {
        Meet meet = Meet.builder()
                .meetName(meetCreateRequestDto.getMeetName())
                .description(meetCreateRequestDto.getDescription())
                .host(user)
                .meetMembers(new ArrayList<>())
                .build();
        return meetRepository.save(meet);
    }

    public Meet getMeet(User user, Long meetId) {
        Meet findMeet = meetRepository.findMeetByIdIfHostedOrInvited(meetId, user.getId());
        if (findMeet == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    MeetErrorMessages.MEET_NOT_FOUND.getMessage()
            );
        }

        return findMeet;
    }

    public MeetMember invite(User currentUser, Long meetId, MeetInviteRequestDto meetInviteRequestDto) {
        Meet meet = meetRepository.findById(meetId).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                MeetErrorMessages.MEET_NOT_FOUND.getMessage()
        ));

        if (!meet.isHost(currentUser.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    MeetErrorMessages.MEET_NOT_FOUND.getMessage()
            );
        }

        if (meet.getMeetMembers().size() >= INVITE_LIMIT_NUMBER) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    MeetErrorMessages.MEET_INVITE_LIMIT_OVER.getMessage()
            );
        }

        String email = meetInviteRequestDto.getEmail();
        User userToInvite = userRepository.findOneByEmail(email).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                UserErrorMessages.USER_NOT_FOUND_BY_EMAIL.getMessage()
        ));

        boolean isUserInvited = meet.getMeetMembers().stream()
                .anyMatch(meetToMember -> meetToMember.getUser().getEmail().equals(email));
        if (isUserInvited) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                MeetErrorMessages.USER_ALREADY_INVITED.getMessage()
            );
        }

        MeetMember meetMember = MeetMember.builder()
                .meet(meet)
                .user(userToInvite)
                .build();
        return meetMemberRepository.save(meetMember);
    }
}
