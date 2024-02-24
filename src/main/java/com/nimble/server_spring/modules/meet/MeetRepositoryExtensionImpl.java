package com.nimble.server_spring.modules.meet;

import com.nimble.server_spring.modules.user.QUser;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MeetRepositoryExtensionImpl implements MeetRepositoryExtension {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Meet> findParticipatedMeets(Long userId) {
        QMeet meet = QMeet.meet;
        QUser user = QUser.user;
        QMeetUser meetUser = QMeetUser.meetUser;

        return jpaQueryFactory.selectFrom(meet)
            .join(meet.meetUsers, meetUser)
            .fetchJoin()
            .join(meetUser.user, user)
            .fetchJoin()
            .where(meet.id.in(
                    JPAExpressions.selectDistinct(meet.id)
                        .from(meet)
                        .join(meet.meetUsers, meetUser)
                        .join(meetUser.user, user)
                        .where(user.id.eq(userId))
                )
            )
            .fetch();
    }
}
