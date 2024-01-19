package com.nimble.server_spring.modules.meet;

import com.nimble.server_spring.infra.persistence.BooleanToYNConverter;
import com.nimble.server_spring.modules.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@ToString(of = {"id", "memberRole", "isEntered"})
public class MeetMember {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @CreatedDate
    private LocalDateTime createdAt;

    @NotNull
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meet_id")
    @NotNull
    private Meet meet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @NotNull
    private User user;

    @Enumerated(EnumType.STRING)
    @NotNull
    private MemberRole memberRole;

    @Convert(converter = BooleanToYNConverter.class)
    @NotNull
    private boolean isEntered = false;

    @Builder
    public MeetMember(Meet meet, User user, MemberRole memberRole) {
        this.meet = meet;
        this.user = user;
        this.memberRole = memberRole;
    }

    public void enterMeet() {
        this.isEntered = true;
    }

    public void leaveMeet() {
        this.isEntered = false;
    }
}
