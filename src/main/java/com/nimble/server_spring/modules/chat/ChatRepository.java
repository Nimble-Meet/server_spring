package com.nimble.server_spring.modules.chat;

import com.nimble.server_spring.modules.chat.dto.response.ChatResponseDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query("select new com.nimble.server_spring.modules.chat.dto.response.ChatResponseDto"
           + "(c.id, mm.id, u.email, c.createdAt, c.chatType, c.message) from Chat c "
           + "join c.meetMember mm "
           + "join mm.user u "
           + "where c.meet.id = :meetId")
    Slice<ChatResponseDto> findAllByMeetId(
        @Param("meetId") Long meetId,
        Pageable pageable
    );
}
