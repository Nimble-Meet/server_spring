package com.nimble.server_spring.modules.meet;

import com.nimble.server_spring.modules.user.QUser;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MeetRepositoryExtensionImpl implements MeetRepositoryExtension {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Meet> findHostedOrInvitedMeetsByUserId(Long userId) {
        QMeet meet = QMeet.meet;
        QUser host = QUser.user;
        QMeetMember meetMember = QMeetMember.meetMember;
        QUser member = new QUser("member");

        return jpaQueryFactory.selectFrom(meet)
            .leftJoin(meet.host, host)
            .fetchJoin()
            .leftJoin(meet.meetMembers, meetMember)
            .fetchJoin()
            .leftJoin(meetMember.user, member)
            .fetchJoin()

            .where(meet.id.in(
                    JPAExpressions
                        .select(meet.id)
                        .from(meet)
                        .where(
                            meet.host.id.eq(userId)
                                .or(member.id.eq(userId))
                        )
                )
            )
            .fetch();
    }

    @Override
    public Optional<Meet> findMeetByIdIfHostedOrInvited(Long meetId, Long userId) {
        QMeet meet = QMeet.meet;
        QUser host = QUser.user;
        QMeetMember meetMember = QMeetMember.meetMember;
        QUser member = new QUser("member");

        Meet fetchedMeet = jpaQueryFactory.selectFrom(meet)
            .leftJoin(meet.host, host)
            .fetchJoin()
            .leftJoin(meet.meetMembers, meetMember)
            .fetchJoin()
            .leftJoin(meetMember.user, member)
            .fetchJoin()
            .where(meet.id.eq(meetId)
                .and(host.id.eq(userId)
                    .or(member.id.eq(userId))
                )
            )
            .fetchOne();
        return Optional.ofNullable(fetchedMeet);
    }
}
