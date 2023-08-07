package com.nimble.server_spring.modules.user;

import com.nimble.server_spring.modules.auth.enums.OauthProvider;
import com.nimble.server_spring.modules.auth.validator.BcryptEncrypted;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class User {
    @Id @GeneratedValue
    private Long id;

    @Column(nullable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column(unique = true, nullable = false)
    @Email
    private String email;

    @BcryptEncrypted
    private String password;

    @Column()
    @Email
    private String nickname;

    @Column
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private OauthProvider providerType = OauthProvider.LOCAL;

    @Column(nullable = true)
    private String providerId;
}
