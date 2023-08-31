package com.nimble.server_spring.modules.meet;

import java.util.List;

public interface MeetRepositoryExtension {
    public List<Meet> findHostedOrInvitedMeetsByUserId(Long userId);
    public Meet findMeetByIdIfHostedOrInvited(Long meetId, Long userId);
}
