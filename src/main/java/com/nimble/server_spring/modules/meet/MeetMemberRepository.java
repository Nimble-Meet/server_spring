package com.nimble.server_spring.modules.meet;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface MeetMemberRepository extends JpaRepository<MeetMember, Long> {

    Optional<MeetMember> findByUserIdAndMeetId(Long userId, Long meetId);

    boolean existsByUser_IdAndMeet_Id(Long userId, Long meetId);
}
