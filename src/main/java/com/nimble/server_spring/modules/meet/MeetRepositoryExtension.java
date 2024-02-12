package com.nimble.server_spring.modules.meet;

import java.util.List;
import java.util.Optional;

public interface MeetRepositoryExtension {

    public List<Meet> findParticipatedMeets(Long userId);

}
