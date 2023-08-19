package com.nimble.server_spring.modules.meet;

import com.nimble.server_spring.modules.meet.dto.request.MeetCreateRequestDto;
import com.nimble.server_spring.modules.user.User;
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
}
