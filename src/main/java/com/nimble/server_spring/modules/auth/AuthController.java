package com.nimble.server_spring.modules.auth;

import com.nimble.server_spring.infra.jwt.AuthTokenProvider;
import com.nimble.server_spring.infra.properties.JwtProperties;
import com.nimble.server_spring.infra.utils.CookieUtils;
import com.nimble.server_spring.infra.utils.HeaderUtils;
import com.nimble.server_spring.modules.auth.dto.request.LocalLoginRequestDto;
import com.nimble.server_spring.modules.auth.dto.request.LocalSignupRequestDto;
import com.nimble.server_spring.modules.auth.dto.response.LoginResponseDto;
import com.nimble.server_spring.modules.auth.dto.response.UserResponseDto;
import com.nimble.server_spring.modules.user.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@EnableConfigurationProperties(JwtProperties.class)
public class AuthController {
    public static final String ACCESS_TOKEN_KEY = "access_token";
    public static final String REFRESH_TOKEN_KEY = "refresh_token";

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final AuthTokenProvider authTokenProvider;
    private final JwtProperties jwtProperties;

    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto> signup(
            @RequestBody LocalSignupRequestDto localSignupDto
            ) {
        User user = authService.signup(localSignupDto);

        UserResponseDto userResponseDto = UserResponseDto.builder()
                .email(user.getEmail())
                .nickname(user.getNickname())
                .providerType(user.getProviderType())
                .build();
        return new ResponseEntity<>(userResponseDto, HttpStatus.CREATED);
    }

    @PostMapping("/login/local")
    public ResponseEntity<LoginResponseDto> login(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestBody LocalLoginRequestDto localLoginDto
    ) {
        JwtToken jwtToken = authService.jwtSign(localLoginDto);

        LoginResponseDto loginResponseDto = LoginResponseDto.builder()
                .userId(jwtToken.getUser().getId())
                .accessToken(jwtToken.getAccessToken())
                .build();
        setJwtTokenCookie(response, jwtToken);
        return new ResponseEntity<>(loginResponseDto, HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refresh(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        Cookie refreshTokenCookie = CookieUtils.getCookie(request, REFRESH_TOKEN_KEY)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        AuthErrorMessages.REFRESH_TOKEN_DOES_NOT_EXIST.getMessage()
                ));
        String refreshToken = refreshTokenCookie.getValue();

        String accessToken = HeaderUtils.resolveBearerTokenFrom(request);
        if(accessToken == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    AuthErrorMessages.ACCESS_TOKEN_DOES_NOT_EXIST.getMessage()
            );
        }

        JwtToken jwtToken = authService.rotateRefreshToken(refreshToken, accessToken);

        setJwtTokenCookie(response, jwtToken);
        LoginResponseDto loginResponseDto = LoginResponseDto.builder()
                .userId(jwtToken.getUser().getId())
                .accessToken(jwtToken.getAccessToken())
                .build();
        return new ResponseEntity<>(loginResponseDto, HttpStatus.OK);
    }

    private void setJwtTokenCookie(HttpServletResponse response, JwtToken jwtToken) {
        // access token의 경우 프론트엔드 단에서 읽을 수 있게 하기 위해 http only를 false로 설정
        CookieUtils.addCookie(response, ACCESS_TOKEN_KEY, jwtToken.getAccessToken(), jwtProperties.getAccessTokenExpiry(), false);
        CookieUtils.addCookie(response, REFRESH_TOKEN_KEY, jwtToken.getRefreshToken(), jwtProperties.getRefreshTokenExpiry(), true);
    }

    @GetMapping("/whoami")
    public ResponseEntity<UserResponseDto> whoami() {
        User currentUser = authService.getCurrentUser();
        UserResponseDto userResponseDto = UserResponseDto.builder()
                .email(currentUser.getEmail())
                .nickname(currentUser.getNickname())
                .providerType(currentUser.getProviderType())
                .build();
        return new ResponseEntity<>(userResponseDto, HttpStatus.OK);
    }

}