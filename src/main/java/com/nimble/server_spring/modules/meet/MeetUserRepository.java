package com.nimble.server_spring.modules.meet;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface MeetUserRepository extends JpaRepository<MeetUser, Long> {

    @EntityGraph(attributePaths = {"user"})
    Optional<MeetUser> findByUser_IdAndMeet_Id(Long userId, Long meetId);

    @Query("select mu from MeetUser mu"
           + " join fetch mu.user u"
           + " where u.email = :email and mu.meet.id = :meetId")
    Optional<MeetUser> findByEmailAndMeetId(
        @Param("email") String email,
        @Param("meetId") Long meetId
    );

    @EntityGraph(attributePaths = {"user"})
    Optional<MeetUser> findMeetUserById(Long id);

    boolean existsByUser_IdAndMeet_Id(Long userId, Long meetId);
}
