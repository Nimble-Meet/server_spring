package com.nimble.server_spring.infra.config;

import com.nimble.server_spring.infra.jwt.JwtAuthenticationFilter;
import com.nimble.server_spring.infra.security.JwtAuthEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final CorsFilter corsFilter;
  private final JwtAuthEntryPoint jwtAuthEntryPoint;

  @Bean
  AuthenticationManager authenticationManager(
      AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity httpSecurity,
      JwtAuthenticationFilter customJwtFilter) throws Exception {
    httpSecurity
        .csrf(AbstractHttpConfigurer::disable)
        .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
        .formLogin(AbstractHttpConfigurer::disable)
        .sessionManagement(configurer -> configurer
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )

        .exceptionHandling(configurer -> configurer
            .authenticationEntryPoint(jwtAuthEntryPoint)
        )

        .authorizeHttpRequests(configurer -> configurer
            .requestMatchers("/api/auth/signup").permitAll()
            .requestMatchers("/api/auth/login/**").permitAll()
            .requestMatchers("/api/auth/refresh").permitAll()
            .requestMatchers("/error").permitAll()
            .requestMatchers("/swagger-ui/**").permitAll()
            .anyRequest().permitAll()
        )

        .addFilterBefore(customJwtFilter, UsernamePasswordAuthenticationFilter.class);

    return httpSecurity.build();
  }
}
