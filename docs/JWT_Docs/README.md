# JWT를 이용한 인증 로직

JWT 토큰을 이용한 인증 시스템을 구축하기 위해서 크게 세가지 작업을 진행했습니다.

- JWT 토큰 발급 및 검증 로직 구현
- 토큰 기반 검증을 수행하는 Security 커스텀 필터 적용
- 로그인, 토큰 재발급 로직 구현

## JWT 토큰 발급 및 검증 로직 구현

JWT 토큰 빌드에는 io.jsonwebtoken 라이브러리를 사용했습니다.  
[AuthTokenManager](https://github.com/Nimble-Meet/server_spring/blob/develop/src/main/java/com/nimble/server_spring/infra/jwt/AuthTokenManager.java)
는 jsonwebtoken 라이브러리를 이용해서 토큰 값을 빌드하거나, 클라이언트에서 전달한 토큰을 검증해서 claim을 추출하는 역할을 수행합니다.  
jwt를 빌드할 때 사용하는 토큰 키와 만료 시간의 경우 환경변수로 삽입되고,
[JwtProperties](https://github.com/Nimble-Meet/server_spring/blob/develop/src/main/java/com/nimble/server_spring/infra/jwt/JwtProperties.java)
에 해당 값들이 담기게 됩니다.  
[JwtConfig](https://github.com/Nimble-Meet/server_spring/blob/develop/src/main/java/com/nimble/server_spring/infra/jwt/JwtConfig.java)
에서 해당 값을 이용하여 AuthTokenManager를 생성한 후, 빈으로 등록합니다.

AuthTokenManager의 public 인터페이스는 다음과 같습니다.

```java
public interface AuthTokenManager {

    AuthToken publishToken(String email, @Nullable String role, JwtTokenType tokenType);

    Optional<Claims> getTokenClaims(String tokenValue, JwtTokenType tokenType);

    Collection<? extends SimpleGrantedAuthority> getAuthorities(Claims claims);
}
```

#### 토큰 발급(publishToken)

publishToken의 경우 유저 식별을 위한 email, 권한 부여를 위한 role, 그리고 토큰 종류(tokenType)를 매개변수로 받습니다.    
[JwtTokenType](https://github.com/Nimble-Meet/server_spring/blob/develop/src/main/java/com/nimble/server_spring/infra/jwt/JwtTokenType.java)
의 경우 ACCESS, REFRESH 로 구성된 enum으로, ACCESS는 인증 처리에 사용하는 access token에 할당되고, REFRESH는 토큰 재발급에 사용하는
refresh token에 할당됩니다.  
토큰 값 빌드 시 토큰 종류에 따라 다른 key와 expiry를 적용하여 빌드합니다.

반환 시에는 토큰 값과 만료 시간을 담은 데이터 객체인
[AuthToken](https://github.com/Nimble-Meet/server_spring/blob/develop/src/main/java/com/nimble/server_spring/infra/jwt/AuthToken.java)
을 반환합니다.  
Filter, Service 등의 계층에서는 해당 객체를 반환 받아서 인증 로직을 구성합니다.

#### 토큰 검증, claim 추출(getTokenClaims)

getTokenClaims는 토큰 값(tokenValue)과 토큰 종류(tokenType)를 받아서, 해당 토큰 값으로부터 claim을 추출합니다.  
access token과 refresh token은 각각 다른 키를 이용해서 빌드했으므로, claim 추출 시에도 다른 키를 이용합니다.  
이 때 유효하지 않은 토큰이라면 Optional.empty()가 반환됩니다.

#### role 정보 추출(getTokenClaims)

getAuthorities의 경우 claim에서 role을 추출하여 SimpleGrantedAuthority 객체로 변환한 후, List로 감싸서 반환합니다.  
Filter 계층에서는 해당 반환값을 이용해 Security 인증 처리를 합니다.

## 토큰 기반 검증을 수행하는 Security 커스텀 필터 적용

이제 인증이 필요한 요청이 온 경우 Security 단에서 jwt access token의 유효성을 판단하여 Authentication을 발급하도록 구현해야 합니다.  
클라이언트에서는 로그인 시 발급받은 access token을 Authorization 헤더에 Bearer Token으로 삽입해서 요청을 보냅니다.  
[JwtAuthFilter](https://github.com/Nimble-Meet/server_spring/blob/develop/src/main/java/com/nimble/server_spring/infra/jwt/JwtAuthFilter.java)
는 요청 헤더에 포함된 토큰 값을 추출하고, 주입받은 authTokenManager 객체를 이용해서 해당 토큰의 유효성을 판단합니다.  
토큰이 유효할 경우에는 UsernamePasswordAuthenticationToken을 발급해서 인증 처리를 하고, 유효하지 않을 경우에는 요청이 거부되도록 합니다.

[SecurityConfig](https://github.com/Nimble-Meet/server_spring/blob/develop/src/main/java/com/nimble/server_spring/infra/security/SecurityConfig.java)
설정을 통해 JwtAuthFilter가 UsernamePasswordAuthenticationFilter 앞에 적용되어 인증 처리를 담당하게 됩니다.  
인증에 실패할 경우 접근이 거부되어
[CustomAuthEntryPoint](https://github.com/Nimble-Meet/server_spring/blob/develop/src/main/java/com/nimble/server_spring/infra/security/CustomAuthEntryPoint.java)
에 정의한 대로 401 에러 응답이 이루어집니다.

## 로그인, 토큰 재발급 로직 구현

[AuthController](https://github.com/Nimble-Meet/server_spring/blob/develop/src/main/java/com/nimble/server_spring/modules/auth/AuthController.java)
는 인증과 관련된 요청을 처리하는 controller 객체입니다.  
로그인(POST /api/auth/login/local), 토큰 재발급(POST /api/auth/refresh) 의 경우 해당 컨트롤러에서 요청을 처리합니다.
