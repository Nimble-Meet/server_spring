package com.nimble.server_spring.modules.meet;

import com.nimble.server_spring.modules.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
@Getter
public class Meet {
    @Id @GeneratedValue
    private Long id;

    @Column
    @NotNull
    @CreatedDate
    private LocalDateTime createdAt;

    @Column
    @NotNull
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column
    @NotNull
    @Length(min = 2, max = 24)
    private String meetName;

    @Column
    @Length(max = 48)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    @JoinColumn(name = "hostId")
    private User host;

    @OneToMany(mappedBy = "meet", cascade = CascadeType.ALL)
    private List<MeetMember> meetMembers = new ArrayList<>();
}
