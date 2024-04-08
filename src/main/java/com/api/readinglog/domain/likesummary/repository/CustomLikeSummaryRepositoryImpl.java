package com.api.readinglog.domain.likesummary.repository;

import static com.api.readinglog.domain.likesummary.entity.QLikeSummary.likeSummary;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomLikeSummaryRepositoryImpl implements CustomLikeSummaryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public long getLikeTotalCountInMonth(Long memberId, int month) {
        return queryFactory
                .select(likeSummary.count())
                .from(likeSummary)
                .where(likeSummary.member.id.eq(memberId).and(likeSummary.createdAt.month().eq(month)))
                .fetchFirst();
    }

}
