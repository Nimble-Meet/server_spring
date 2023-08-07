package com.nimble.server_spring.modules.auth;

import com.nimble.server_spring.modules.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class JwtToken {
    @Id @GeneratedValue
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
    @JoinColumn(name = "userId")
    private User user;

    boolean equalsAccessToken(String accessToken) {
        return this.accessToken.equals(accessToken);
    }
}
