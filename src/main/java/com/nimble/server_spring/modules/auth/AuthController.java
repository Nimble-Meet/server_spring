package com.nimble.server_spring.modules.auth;

import com.nimble.server_spring.infra.error.ErrorCodeException;
import com.nimble.server_spring.infra.properties.JwtProperties;
import com.nimble.server_spring.infra.utils.CookieUtils;
import com.nimble.server_spring.infra.utils.HeaderUtils;
import com.nimble.server_spring.modules.auth.dto.request.LocalLoginRequestDto;
import com.nimble.server_spring.modules.auth.dto.request.LocalSignupRequestDto;
import com.nimble.server_spring.modules.auth.dto.response.LoginResponseDto;
import com.nimble.server_spring.modules.auth.dto.response.UserResponseDto;
import com.nimble.server_spring.modules.user.User;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

  public static final String ACCESS_TOKEN_KEY = "access_token";
  public static final String REFRESH_TOKEN_KEY = "refresh_token";

  private final AuthService authService;
  private final JwtProperties jwtProperties;

  @PostMapping("/signup")
  public ResponseEntity<UserResponseDto> signup(
      @RequestBody @Parameter(description = "회원 가입 정보", required = true)
      LocalSignupRequestDto localSignupDto
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
      HttpServletResponse response,
      @RequestBody @Parameter(description = "로그인 정보", required = true)
      LocalLoginRequestDto localLoginDto
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
        .orElseThrow(() -> new ErrorCodeException(AuthErrorCode.REFRESH_TOKEN_DOES_NOT_EXIST));
    String refreshToken = refreshTokenCookie.getValue();

    String accessToken = HeaderUtils.resolveBearerTokenFrom(request);
    if (accessToken == null) {
      throw new ErrorCodeException(AuthErrorCode.ACCESS_TOKEN_DOES_NOT_EXIST);
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
    CookieUtils.addCookie(response, ACCESS_TOKEN_KEY, jwtToken.getAccessToken(),
        jwtProperties.getAccessTokenExpiry(), false);
    CookieUtils.addCookie(response, REFRESH_TOKEN_KEY, jwtToken.getRefreshToken(),
        jwtProperties.getRefreshTokenExpiry(), true);
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

  @PostMapping("/logout")
  public ResponseEntity<UserResponseDto> logout(
      HttpServletRequest request,
      HttpServletResponse response
  ) {
    User currentUser = authService.getCurrentUser();
    UserResponseDto userResponseDto = UserResponseDto.builder()
        .email(currentUser.getEmail())
        .nickname(currentUser.getNickname())
        .providerType(currentUser.getProviderType())
        .build();

    CookieUtils.deleteCookie(request, response, ACCESS_TOKEN_KEY);
    CookieUtils.deleteCookie(request, response, REFRESH_TOKEN_KEY);
    return new ResponseEntity<>(userResponseDto, HttpStatus.OK);
  }
}