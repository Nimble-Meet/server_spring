package com.nimble.server_spring.modules.auth.dto.request;

import com.nimble.server_spring.infra.security.RoleType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PublishTokenServiceRequest {

    @NotNull
    private final Long userId;

    @NotNull
    private final RoleType roleType;

    @Builder
    private PublishTokenServiceRequest(Long userId, RoleType roleType) {
        this.userId = userId;
        this.roleType = roleType;
    }

    public static PublishTokenServiceRequest create(Long userId, RoleType roleType) {
        return PublishTokenServiceRequest.builder()
            .userId(userId)
            .roleType(roleType)
            .build();
    }
}
