package com.nimble.server_spring.modules.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface JwtTokenRepository extends JpaRepository<JwtToken, Long>, QuerydslPredicateExecutor<JwtToken> {
    JwtToken findOneByRefreshToken(String refreshToken);
    JwtToken findOneByUserId(Long userId);
}
