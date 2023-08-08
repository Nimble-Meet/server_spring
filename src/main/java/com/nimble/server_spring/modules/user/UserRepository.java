package com.nimble.server_spring.modules.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface UserRepository extends JpaRepository<User, Long>, QuerydslPredicateExecutor<User> {
    User findOneByEmail(String email);
    boolean existsByEmail(String email);
}
