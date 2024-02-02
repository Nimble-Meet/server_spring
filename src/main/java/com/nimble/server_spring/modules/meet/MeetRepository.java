package com.nimble.server_spring.modules.meet;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface MeetRepository extends JpaRepository<Meet, Long>, MeetRepositoryExtension {

    @EntityGraph(attributePaths = {"meetUsers", "meetUsers.user"})
    Optional<Meet> findMeetById(Long id);
}
