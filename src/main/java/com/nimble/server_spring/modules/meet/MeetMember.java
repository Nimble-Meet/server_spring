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
@EqualsAndHashCode(of = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
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
    @NotNull
    @JoinColumn(name = "meet_id")
    private Meet meet;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private MemberRole memberRole;

    @Convert(converter = BooleanToYNConverter.class)
    private boolean isEntered;

    @Override
    public String toString() {
        return "MeetMember{" +
               "id=" + id +
               ", createdAt=" + createdAt +
               ", updatedAt=" + updatedAt +
               '}';
    }

    public void enterMeet() {
        this.isEntered = true;
    }

    public void leaveMeet() {
        this.isEntered = false;
    }
}
