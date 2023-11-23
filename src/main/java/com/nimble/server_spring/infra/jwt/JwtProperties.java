package com.nimble.server_spring.infra.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String accessTokenSecret;
    private Integer accessTokenExpiry;
    private String refreshTokenSecret;
    private Integer refreshTokenExpiry;
}
