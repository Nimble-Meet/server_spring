package com.nimble.server_spring.modules.chat;

import com.nimble.server_spring.modules.meet.Meet;
import com.nimble.server_spring.modules.meet.MeetMember;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@ToString(of = {"id", "chatType", "email", "message"})
public class Chat {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    @NotNull
    @CreatedDate
    private LocalDateTime createdAt;

    @Column
    @NotNull
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @NotNull
    private ChatType chatType;

    @NotNull
    private String email;

    private String message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meet_id")
    @NotNull
    private Meet meet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @NotNull
    private MeetMember meetMember;


    @Builder
    public Chat(
        ChatType chatType,
        String email,
        String message,
        Meet meet,
        MeetMember meetMember
    ) {
        this.chatType = chatType;
        this.email = email;
        this.message = message;
        this.meet = meet;
        this.meetMember = meetMember;
    }
}
