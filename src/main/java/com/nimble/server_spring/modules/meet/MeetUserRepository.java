package com.nimble.server_spring.modules.meet;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface MeetUserRepository extends JpaRepository<MeetUser, Long> {

    @EntityGraph(attributePaths = {"user"})
    Optional<MeetUser> findByUserIdAndMeetId(Long userId, Long meetId);

    @EntityGraph(attributePaths = {"user"})
    Optional<MeetUser> findMeetUserById(Long id);

    boolean existsByUser_IdAndMeet_Id(Long userId, Long meetId);
}
