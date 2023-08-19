package com.nimble.server_spring.modules.meet;

import com.nimble.server_spring.modules.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MeetService {

    private final MeetRepository meetRepository;
    public List<Meet> getHostedOrInvitedMeets(User user) {
        return meetRepository.findHostedOrInvitedMeetsByUserId(user.getId());
    }
}
