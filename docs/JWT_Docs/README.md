# JWT를 이용한 인증 로직

JWT 토큰을 이용한 인증 시스템을 구축하기 위해서 크게 세가지 기능을 구현해야 합니다.

- JWT 토큰 발급 및 검증 로직
- Spring Security 커스텀 필터 적용 및 예외 처리
- 각 endpoint에 대한 controller 구현

## JWT 토큰 발급 및 검증 로직

JWT를 구현하기 위해서 io.jsonwebtoken 라이브러리를 사용했습니다.  
[AuthTokenManager](https://github.com/Nimble-Meet/server_spring/blob/develop/src/main/java/com/nimble/server_spring/infra/jwt/AuthTokenManager.java)
는 해당 라이브러리를 이용하여 토큰 값을 빌드하고, 토큰에서 claim을 추출해서 검증하는 역할을 담당합니다.

jwt를 빌드할 때 사용하는 토큰 키와 만료 시간의 경우 환경변수로
삽입되고, [JwtProperties](https://github.com/Nimble-Meet/server_spring/blob/develop/src/main/java/com/nimble/server_spring/infra/jwt/JwtProperties.java)
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

publishToken의 경우 유저 식별을 위한 email, 권한 부여를 위한 role, 그리고 토큰 종류 구분을 위한 tokenType을 매개변수로 받습니다.    
[JwtTokenType](https://github.com/Nimble-Meet/server_spring/blob/develop/src/main/java/com/nimble/server_spring/infra/jwt/JwtTokenType.java)
의 경우 ACCESS, REFRESH 두 값으로 구성된 enum입니다.  
토큰 값 빌드 시 토큰 종류에 따라 다른 key와 expiry를 적용하여 빌드합니다.

반환 시에는 토큰 값과 만료 시간을 담은
[AuthToken](https://github.com/Nimble-Meet/server_spring/blob/develop/src/main/java/com/nimble/server_spring/infra/jwt/AuthToken.java)
이라는 데이터 객체를 반환합니다.  
Filter, Service 등의 계층에서는 토큰 발급 시 해당 객체를 받아서 인증 로직을 구성합니다.

#### 토큰 검증, claim 추출(getTokenClaims)

getTokenClaims의 경우 토큰 값(tokenValue)과 토큰 종류(tokenType)를 받아서, 해당 토큰으로부터 claim을 추출합니다.  
이 때 유효하지 않은 토큰이라면 Optional.empty()가 반환되기 때문에, 해당 반환값을 통해 토큰이 유효한지를 확인할 수 있습니다.

#### role 정보 추출(getTokenClaims)

getAuthorities의 경우 claim에서 role을 추출하여 SimpleGrantedAuthority 객체로 변환한 후, List로 감싸서 반환합니다.  
Filter 계층에서는 해당 반환값을 이용해 UsernamePasswordAuthenticationToken을 발급하고, Security 인증 처리를 합니다.