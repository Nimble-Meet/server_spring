package com.nimble.server_spring.modules.chat;

import com.nimble.server_spring.infra.persistence.BaseEntity;
import com.nimble.server_spring.modules.meet.Meet;
import com.nimble.server_spring.modules.meet.MeetUser;
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
    @JoinColumn(name = "meet_user_id")
    @NotNull
    private MeetUser meetUser;


    @Builder
    public Chat(
        ChatType chatType,
        String message,
        Meet meet,
        MeetUser meetUser
    ) {
        this.chatType = chatType;
        this.message = message;
        this.meet = meet;
        this.meetUser = meetUser;
    }
}
