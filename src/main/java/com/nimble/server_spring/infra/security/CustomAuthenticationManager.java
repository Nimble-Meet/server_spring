package com.nimble.server_spring.infra.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
@Slf4j
public class CustomAuthenticationManager implements AuthenticationManager {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(
        Authentication authentication
    ) throws AuthenticationException {
        String email;
        String password;
        try {
            email = (String) authentication.getPrincipal();
            password = (String) authentication.getCredentials();
        } catch (Exception exception) {
            log.error(
                "인증 과정에서 타입 변환에 실패했습니다. - email: {}, password type: {}",
                authentication.getPrincipal(),
                authentication.getCredentials().getClass(),
                exception
            );
            throw new BadCredentialsException(
                "인증 과정에서 타입 변환에 실패했습니다. : " + exception.getMessage(),
                exception
            );
        }

        UserDetails userDetails;
        try {
            userDetails = userDetailsService.loadUserByUsername(email);
        } catch (AuthenticationException exception) {
            throw exception;
        } catch (Exception exception) {
            log.error("UserDetails 조회에 실패했습니다. - email: {}", email, exception);
            throw new BadCredentialsException("UserDetails 조회에 실패했습니다.", exception);
        }

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }

        return new UsernamePasswordAuthenticationToken(
            userDetails.getUsername(),
            userDetails.getPassword(),
            userDetails.getAuthorities()
        );
    }
}
