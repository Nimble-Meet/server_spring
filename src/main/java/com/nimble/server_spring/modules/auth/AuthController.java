package com.nimble.server_spring.modules.auth;

import static com.nimble.server_spring.infra.apidoc.SwaggerConfig.JWT_ACCESS_TOKEN;
import static com.nimble.server_spring.modules.auth.TokenCookieFactory.REFRESH_TOKEN_COOKIE_KEY;

import com.nimble.server_spring.infra.apidoc.ApiErrorCodes;
import com.nimble.server_spring.infra.error.ErrorCode;
import com.nimble.server_spring.infra.error.ErrorCodeException;
import com.nimble.server_spring.infra.http.ApiResponse;
import com.nimble.server_spring.infra.jwt.JwtProperties;
import com.nimble.server_spring.infra.http.CookieParser;
import com.nimble.server_spring.infra.http.BearerTokenParser;
import com.nimble.server_spring.infra.security.dto.request.LocalLoginRequestDto;
import com.nimble.server_spring.modules.auth.dto.request.LocalSignupRequest;
import com.nimble.server_spring.infra.security.dto.response.LoginResponse;
import com.nimble.server_spring.modules.auth.dto.request.RotateTokenServiceRequest;
import com.nimble.server_spring.modules.auth.dto.response.JwtTokenResponse;
import com.nimble.server_spring.modules.auth.dto.response.UserResponse;
import com.nimble.server_spring.modules.user.User;
import com.nimble.server_spring.modules.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@EnableConfigurationProperties(JwtProperties.class)
@Tag(name = "Auth", description = "인증 관련 API")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final TokenCookieFactory tokenCookieFactory;

    @PostMapping("/signup")
    @Operation(summary = "이메일 + 비밀 번호 회원 가입", description = "이메일 + 비밀번호로 회원 가입을 합니다.")
    @ApiErrorCodes({
        ErrorCode.EMAIL_ALREADY_EXISTS,
        ErrorCode.NOT_SHA256_ENCRYPTED
    })
    public ApiResponse<UserResponse> signup(
        @RequestBody @Validated @Parameter(description = "회원 가입 정보", required = true)
        LocalSignupRequest localSignupRequest
    ) {
        UserResponse userResponse = authService.signup(localSignupRequest.toServiceRequest());
        return ApiResponse.ok(userResponse);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Access Token 토큰 갱신", description = "refresh token 유효성 검증 후 access token을 갱신합니다.")
    @ApiErrorCodes({
        ErrorCode.ACCESS_TOKEN_DOES_NOT_EXIST,
        ErrorCode.REFRESH_TOKEN_DOES_NOT_EXIST,
        ErrorCode.INVALID_REFRESH_TOKEN,
        ErrorCode.INCONSISTENT_ACCESS_TOKEN,
        ErrorCode.EXPIRED_REFRESH_TOKEN,
    })
    @SecurityRequirement(name = JWT_ACCESS_TOKEN)
    public ApiResponse<LoginResponse> refresh(
        HttpServletRequest request,
        HttpServletResponse response
    ) {
        String accessToken = BearerTokenParser.from(request).getToken()
            .orElseThrow(() -> new ErrorCodeException(ErrorCode.ACCESS_TOKEN_DOES_NOT_EXIST));

        Cookie refreshTokenCookie = CookieParser.from(request).getCookie(REFRESH_TOKEN_COOKIE_KEY)
            .orElseThrow(() -> new ErrorCodeException(ErrorCode.REFRESH_TOKEN_DOES_NOT_EXIST));
        String refreshToken = refreshTokenCookie.getValue();

        JwtTokenResponse jwtTokenResponse
            = authService.rotateToken(RotateTokenServiceRequest.create(accessToken, refreshToken));

        response.addCookie(
            tokenCookieFactory.createAccessTokenCookie(jwtTokenResponse.getAccessToken())
        );
        response.addCookie(
            tokenCookieFactory.createRefreshTokenCookie(jwtTokenResponse.getRefreshToken())
        );
        return ApiResponse.ok(LoginResponse.fromJwtToken(jwtTokenResponse));
    }

    @GetMapping("/whoami")
    @Operation(summary = "현재 사용자 정보 조회", description = "현재 사용자 정보를 조회합니다.")
    @ApiErrorCodes({
        ErrorCode.UNAUTHENTICATED_REQUEST,
        ErrorCode.USER_NOT_FOUND,
    })
    @SecurityRequirement(name = JWT_ACCESS_TOKEN)
    public ApiResponse<UserResponse> whoami(Principal principal) {
        User currentUser = userService.getUserByPrincipal(principal);

        return ApiResponse.ok(UserResponse.fromUser(currentUser));
    }

    @PostMapping("/login/local")
    @Operation(summary = "이메일 + 비밀 번호 로그인", description = "이메일 + 비밀번호로 로그인을 합니다.")
    @ApiErrorCodes({ErrorCode.LOGIN_FAILED})
    public ApiResponse<LoginResponse> login(
        @RequestBody @Validated @Parameter(description = "로그인 정보", required = true)
        LocalLoginRequestDto localLoginDto
    ) {
        // LocalLoginFilter에서 처리
        return null;
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "쿠키의 access token과 refresh token을 삭제 하여 로그아웃 합니다.")
    public ApiResponse<Object> logout() {
        // Spring Security에서 처리
        return null;
    }
}