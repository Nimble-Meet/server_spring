package com.nimble.server_spring.modules.chat;

import com.nimble.server_spring.infra.persistence.BaseEntity;
import com.nimble.server_spring.modules.meet.Meet;
import com.nimble.server_spring.modules.meet.MeetMember;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "chatType", "message"})
public class Chat extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    @NotNull
    private ChatType chatType;

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
        String message,
        Meet meet,
        MeetMember meetMember
    ) {
        this.chatType = chatType;
        this.message = message;
        this.meet = meet;
        this.meetMember = meetMember;
    }
}
