package com.nimble.server_spring.modules.user;

import com.nimble.server_spring.modules.auth.enums.OauthProvider;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@ToString(of = {"id", "email", "nickname"})
public class User {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @CreatedDate
    private LocalDateTime createdAt;

    @NotNull
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column(unique = true)
    @NotNull
    @Email
    private String email;

    @Pattern(regexp = "^\\$2[ayb]\\$.{56}$", message = "비밀번호는 BCrpyt로 암호화된 문자열이어야 합니다.")
    private String password;

    @NotNull
    private String nickname;

    @Enumerated(EnumType.STRING)
    @NotNull
    private OauthProvider providerType;

    private String providerId;

    @Builder
    public User(
        String email,
        String password,
        String nickname,
        OauthProvider providerType,
        String providerId
    ) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.providerType = providerType;
        this.providerId = providerId;
    }
}
