package com.nimble.server_spring.modules.meet;

import com.nimble.server_spring.infra.persistence.BaseEntity;
import com.nimble.server_spring.modules.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Meet extends BaseEntity {

    @Id
    @GeneratedValue
    @ToString.Include
    private Long id;

    @NotNull
    @Length(min = 2, max = 24)
    @ToString.Include
    private String meetName;

    @Column(unique = true)
    @NotNull
    @ToString.Include
    @EqualsAndHashCode.Include
    private final UUID code = UUID.randomUUID();

    @Column
    @Length(max = 48)
    private String description;

    @OneToMany(mappedBy = "meet", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @NotNull
    private List<MeetUser> meetUsers = new ArrayList<>();

    @Builder
    private Meet(String meetName, String description) {
        this.meetName = meetName;
        this.description = description;
    }

    public static Meet create(String meetName, String description, User hostUser) {
        Meet meet = Meet.builder()
            .meetName(meetName)
            .description(description)
            .build();
        meet.addUser(hostUser, MeetUserRole.HOST);
        return meet;
    }

    public boolean isHostedBy(User user) {
        return this.meetUsers.stream()
            .filter(MeetUser::isHost)
            .findFirst()
            .map(MeetUser::getUser)
            .map(user::equals)
            .orElse(false);
    }

    public boolean isParticipatedBy(User user) {
        return this.meetUsers.stream()
            .map(MeetUser::getUser)
            .anyMatch(user::equals);
    }

    public void addParticipant(User user) {
        addUser(user, MeetUserRole.PARTICIPANT);
    }

    public void removeParticipant(User user) {
        if (isHostedBy(user)) {
            throw new IllegalStateException("미팅 호스트는 제외할 수 없습니다.");
        }
        meetUsers.removeIf(meetUser -> meetUser.getUser().equals(user));
    }

    private void addUser(User user, MeetUserRole meetUserRole) {
        if (isParticipatedBy(user)) {
            throw new IllegalStateException("이미 참가한 사용자입니다.");
        }
        MeetUser meetUser = MeetUser.builder()
            .meet(this)
            .user(user)
            .meetUserRole(meetUserRole)
            .build();
        meetUsers.add(meetUser);
    }
}
