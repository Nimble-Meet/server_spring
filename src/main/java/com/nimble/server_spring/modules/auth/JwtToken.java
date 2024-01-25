package com.nimble.server_spring.modules.auth;

import com.nimble.server_spring.infra.jwt.AuthToken;
import com.nimble.server_spring.infra.persistence.BaseEntity;
import com.nimble.server_spring.modules.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "accessToken", "refreshToken", "expiresAt"})
public class JwtToken extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    @NotNull
    @NotBlank
    private String accessToken;

    @Column(unique = true)
    @NotNull
    @NotBlank
    private String refreshToken;

    @NotNull
    private LocalDateTime expiresAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @NotNull
    private User user;

    @Builder
    public JwtToken(String accessToken, String refreshToken, LocalDateTime expiresAt, User user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresAt = expiresAt;
        this.user = user;
    }

    boolean equalsAccessToken(String accessToken) {
        return this.accessToken.equals(accessToken);
    }

    public static JwtToken issue(AuthToken accessToken, AuthToken refreshToken, User user) {
        return builder()
            .accessToken(accessToken.getToken())
            .refreshToken(refreshToken.getToken())
            .expiresAt(refreshToken.getExpiresAt())
            .user(user)
            .build();
    }

    public JwtToken reissue(AuthToken accessToken, AuthToken refreshToken) {
        this.accessToken = accessToken.getToken();
        this.refreshToken = refreshToken.getToken();
        this.expiresAt = refreshToken.getExpiresAt();
        return this;
    }
}
