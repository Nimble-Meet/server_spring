package com.nimble.server_spring.infra.jwt;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfig {

    @Bean
    public AuthTokenManager tokenProvider(JwtProperties jwtProperties) {
        return new AuthTokenManager(
            jwtProperties.getAccessTokenSecret(),
            jwtProperties.getRefreshTokenSecret(),
            jwtProperties.getAccessTokenExpiry(),
            jwtProperties.getRefreshTokenExpiry()
        );
    }
}
