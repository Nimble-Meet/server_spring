package com.nimble.server_spring.modules.meet;

import com.nimble.server_spring.infra.persistence.BaseEntity;
import com.nimble.server_spring.modules.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "meetName", "description"})
public class Meet extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    @NotNull
    @Length(min = 2, max = 24)
    private String meetName;

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
            .filter(meetUser -> meetUser.getUser().getId().equals(user.getId()))
            .findFirst()
            .map(MeetUser::isHost)
            .orElse(false);
    }

    public boolean isParticipatedBy(User user) {
        return this.meetUsers.stream()
            .anyMatch(meetUser -> meetUser.getUser().getId().equals(user.getId()));
    }

    public Optional<MeetUser> findMeetUser(Long meetUserId) {
        return this.meetUsers.stream()
            .filter(meetUser -> meetUser.getId().equals(meetUserId))
            .findFirst();
    }

    public void addMeetUser(MeetUser meetUser) {
        this.meetUsers.add(meetUser);
    }
}
