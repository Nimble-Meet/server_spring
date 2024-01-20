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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id")
    @NotNull
    private User host;

    @OneToMany(mappedBy = "meet", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @NotNull
    private List<MeetMember> meetMembers = new ArrayList<>();

    @Builder
    public Meet(String meetName, String description, User host) {
        this.meetName = meetName;
        this.description = description;
        this.host = host;
    }

    public boolean isHost(User user) {
        return this.host.getId().equals(user.getId());
    }

    public Optional<MeetMember> findMember(Long memberId) {
        return this.meetMembers.stream()
            .filter(meetMember -> meetMember.getId().equals(memberId))
            .findFirst();
    }

    public void addMeetMember(MeetMember meetMember) {
        this.meetMembers.add(meetMember);
    }
}
