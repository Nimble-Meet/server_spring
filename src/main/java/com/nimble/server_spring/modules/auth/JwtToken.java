package com.nimble.server_spring.modules.auth;

import com.nimble.server_spring.infra.jwt.AuthToken;
import com.nimble.server_spring.modules.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@EqualsAndHashCode(of = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class JwtToken {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    @NotBlank
    private String accessToken;

    @Column(unique = true, nullable = false)
    @NotBlank
    private String refreshToken;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @OneToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(name = "user_id")
    private Long userId;

    boolean equalsAccessToken(String accessToken) {
        return this.accessToken.equals(accessToken);
    }

    public static JwtToken issue(AuthToken accessToken, AuthToken refreshToken, Long userId) {
        return builder()
            .accessToken(accessToken.getToken())
            .refreshToken(refreshToken.getToken())
            .expiresAt(refreshToken.getExpiresAt())
            .userId(userId)
            .build();
    }

    public JwtToken reissue(AuthToken accessToken, AuthToken refreshToken) {
        this.accessToken = accessToken.getToken();
        this.refreshToken = refreshToken.getToken();
        this.expiresAt = refreshToken.getExpiresAt();
        return this;
    }
}
