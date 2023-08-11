package com.nimble.server_spring.infra.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String accessTokenSecret;
    private Long accessTokenExpiry;
    private String refreshTokenSecret;
    private Long refreshTokenExpiry;
}
