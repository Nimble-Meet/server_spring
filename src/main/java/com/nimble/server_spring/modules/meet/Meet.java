package com.nimble.server_spring.modules.meet;

import com.nimble.server_spring.infra.persistence.BaseEntity;
import com.nimble.server_spring.modules.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;

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
    public Meet(String meetName, String description) {
        this.meetName = meetName;
        this.description = description;
    }

    public boolean isHostedBy(User user) {
        return this.meetUsers.stream()
            .filter(meetUser -> meetUser.getUser().equals(user))
            .findFirst()
            .map(MeetUser::isHost)
            .orElse(false);
    }

    public boolean isParticipatedBy(User user) {
        return this.meetUsers.stream()
            .anyMatch(meetUser -> meetUser.getUser().equals(user));
    }

    public void addUser(User user, MeetUserRole meetUserRole) {
        MeetUser meetUser = MeetUser.builder()
            .meet(this)
            .user(user)
            .meetUserRole(meetUserRole)
            .build();
        meetUsers.add(meetUser);
    }

    public boolean hasMeetUser(MeetUser meetUser) {
        return meetUsers.contains(meetUser);
    }

    public void removeMeetUser(MeetUser meetUser) {
        meetUsers.remove(meetUser);
    }
}
