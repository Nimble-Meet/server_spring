package com.nimble.server_spring.modules.meet;

import com.nimble.server_spring.infra.persistence.BaseEntity;
import com.nimble.server_spring.infra.persistence.BooleanToYNConverter;
import com.nimble.server_spring.modules.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "meetUserRole", "isEntered"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class MeetUser extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meet_id")
    @NotNull
    @EqualsAndHashCode.Include
    private Meet meet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @NotNull
    @EqualsAndHashCode.Include
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
