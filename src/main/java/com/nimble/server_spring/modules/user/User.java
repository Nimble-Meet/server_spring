package com.nimble.server_spring.modules.user;

import com.nimble.server_spring.infra.persistence.BaseEntity;
import com.nimble.server_spring.modules.auth.enums.OauthProvider;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class User extends BaseEntity {

    @Id
    @GeneratedValue
    @ToString.Include
    private Long id;

    @Column(unique = true)
    @NotNull
    @Email
    @ToString.Include
    @EqualsAndHashCode.Include
    private String email;

    @Pattern(regexp = "^\\$2[ayb]\\$.{56}$", message = "비밀번호는 BCrpyt로 암호화된 문자열이어야 합니다.")
    private String password;

    @NotNull
    @ToString.Include
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
