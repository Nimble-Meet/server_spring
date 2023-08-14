package com.nimble.server_spring.modules.auth;

import com.nimble.server_spring.infra.jwt.AuthTokenProvider;
import com.nimble.server_spring.modules.auth.dto.request.LocalLoginRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final AuthTokenProvider authTokenProvider;

    @PostMapping("/login/local")
    public ResponseEntity login(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestBody LocalLoginRequestDto localLoginDto
    ) {
        JwtToken jwtToken = authService.jwtSign(localLoginDto);

        return new ResponseEntity<>(jwtToken, HttpStatus.OK);
    }

}