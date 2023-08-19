package com.nimble.server_spring.infra.config;

import com.nimble.server_spring.infra.jwt.AuthTokenProvider;
import com.nimble.server_spring.infra.properties.JwtProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfig {
    @Bean
    public AuthTokenProvider tokenProvider(JwtProperties jwtProperties) {
        return new AuthTokenProvider(
                jwtProperties.getAccessTokenSecret(),
                jwtProperties.getRefreshTokenSecret(),
                jwtProperties.getAccessTokenExpiry(),
                jwtProperties.getRefreshTokenExpiry()
        );
    }
}
