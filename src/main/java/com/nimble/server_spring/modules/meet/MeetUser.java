package com.nimble.server_spring.modules.meet;

import com.nimble.server_spring.infra.persistence.BaseEntity;
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
@ToString(of = {"id", "meetUserRole", "isEntered"})
public class MeetUser extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

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
    private MeetUserRole meetUserRole;

    @Convert(converter = BooleanToYNConverter.class)
    @NotNull
    private boolean isEntered = false;

    @Builder
    public MeetUser(Meet meet, User user, MeetUserRole meetUserRole) {
        this.meet = meet;
        this.user = user;
        this.meetUserRole = meetUserRole;
    }

    public boolean isHost() {
        return this.meetUserRole == MeetUserRole.HOST;
    }

    public void enterMeet() {
        this.isEntered = true;
    }

    public void leaveMeet() {
        this.isEntered = false;
    }
}
