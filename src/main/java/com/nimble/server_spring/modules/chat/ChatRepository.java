package com.nimble.server_spring.modules.chat;

import com.nimble.server_spring.modules.chat.dto.response.ChatResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query("select new com.nimble.server_spring.modules.chat.dto.response.ChatResponse"
           + "(c.id, mu.id, mu.meet.id, u.email, c.createdAt, c.chatType, c.message) from Chat c "
           + "join c.meetUser mu "
           + "join mu.user u "
           + "where mu.meet.id = :meetId")
    Slice<ChatResponse> findAllByMeetId(
        @Param("meetId") Long meetId,
        Pageable pageable
    );
}
