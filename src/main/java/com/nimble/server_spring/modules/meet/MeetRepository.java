package com.nimble.server_spring.modules.meet;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface MeetRepository extends JpaRepository<Meet, Long>, MeetRepositoryExtension {
}
