package com.nimble.server_spring.infra.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimble.server_spring.infra.jwt.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsFilter corsFilter;
    private final CustomAuthEntryPoint authEntryPoint;
    private final CustomUserDetailsService userDetailsService;
    private final CustomAuthenticationSuccessHandler authenticationSuccessHandler;
    private final CustomAuthenticationFailureHandler authenticationFailureHandler;
    private final ObjectMapper objectMapper;

    @Bean
    public UsernamePasswordAuthenticationFilter customAuthenticationProcessingFilter() {
        LocalLoginFilter filter = new LocalLoginFilter(
            new LocalLoginAuthenticationManager(userDetailsService, new BCryptPasswordEncoder()),
            objectMapper
        );
        filter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        filter.setAuthenticationFailureHandler(authenticationFailureHandler);
        filter.setRequiresAuthenticationRequestMatcher(
            new AntPathRequestMatcher("/api/auth/login/local", "POST")
        );
        return filter;
    }

    @Bean
    public SecurityFilterChain filterChain(
        HttpSecurity httpSecurity,
        JwtAuthFilter customJwtFilter
    ) throws Exception {
        httpSecurity
            .csrf(AbstractHttpConfigurer::disable)
            .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
            .formLogin(AbstractHttpConfigurer::disable)
            .sessionManagement(configurer -> configurer
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            .exceptionHandling(configurer -> configurer
                .authenticationEntryPoint(authEntryPoint)
            )

            .authorizeHttpRequests(configurer -> configurer
                .requestMatchers("/api/auth/signup").permitAll()
                .requestMatchers("/api/auth/login/**").permitAll()
                .requestMatchers("/api/auth/refresh").permitAll()
                .requestMatchers("/error").permitAll()
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/api-docs/**").permitAll()
                .requestMatchers("/ws/**").permitAll()
                .anyRequest().authenticated()
            )

            .addFilterBefore(customJwtFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }
}
