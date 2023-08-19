package com.nimble.server_spring.modules.meet;

import com.nimble.server_spring.modules.meet.dto.request.MeetCreateRequestDto;
import com.nimble.server_spring.modules.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
