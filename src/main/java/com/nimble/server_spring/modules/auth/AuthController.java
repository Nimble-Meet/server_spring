package com.nimble.server_spring.modules.auth;

import com.nimble.server_spring.infra.jwt.AuthTokenProvider;
import com.nimble.server_spring.infra.properties.JwtProperties;
import com.nimble.server_spring.infra.utils.CookieUtils;
import com.nimble.server_spring.modules.auth.dto.request.LocalLoginRequestDto;
import com.nimble.server_spring.modules.auth.dto.request.LocalSignupRequestDto;
import com.nimble.server_spring.modules.auth.dto.response.LoginResponseDto;
import com.nimble.server_spring.modules.auth.dto.response.UserResponseDto;
import com.nimble.server_spring.modules.user.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
@EnableConfigurationProperties(JwtProperties.class)
public class AuthController {
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final AuthTokenProvider authTokenProvider;
    private final JwtProperties jwtProperties;

    @PostMapping("/signup")
    public ResponseEntity signup(
            @RequestBody LocalSignupRequestDto localSignupDto
            ) {
        User user = authService.signup(localSignupDto);

        UserResponseDto userResponseDto = UserResponseDto.builder()
                .email(user.getEmail())
                .nickname(user.getNickname())
                .providerType(user.getProviderType())
                .build();
        return new ResponseEntity(userResponseDto, HttpStatus.CREATED);
    }

    @PostMapping("/login/local")
    public ResponseEntity login(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestBody LocalLoginRequestDto localLoginDto
    ) {
        JwtToken jwtToken = authService.jwtSign(localLoginDto);

        LoginResponseDto loginResponseDto = LoginResponseDto.builder()
                .userId(jwtToken.getUser().getId())
                .accessToken(jwtToken.getAccessToken())
                .build();
        CookieUtils.addCookie(response, "access_token", jwtToken.getAccessToken(), jwtProperties.getAccessTokenExpiry());
        CookieUtils.addCookie(response, "refresh_token", jwtToken.getRefreshToken(), jwtProperties.getRefreshTokenExpiry());
        return new ResponseEntity<>(loginResponseDto, HttpStatus.OK);
    }

}