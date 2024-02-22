package com.nimble.server_spring.modules.meet;

import java.util.List;

public interface MeetRepositoryExtension {

    public List<Meet> findParticipatedMeets(Long userId);

}
