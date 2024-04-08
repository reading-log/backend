package com.api.readinglog.domain.book.repository;

import static com.api.readinglog.domain.book.entity.QBook.book;

import com.api.readinglog.domain.book.dto.BookCalendarResponse;
import com.api.readinglog.domain.book.dto.BookCategoryResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomBookRepositoryImpl implements CustomBookRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public Long getBookTotalCountInMonth(long memberId, int month) {
        return queryFactory
                .select(book.count())
                .from(book)
                .where(book.member.id.eq(memberId).and(book.createdAt.month().eq(month)))
                .fetchFirst();
    }

    @Override
    public List<BookCategoryResponse> getBookCountGroupByCategory(Long memberId, int month) {
        return queryFactory
                .select(Projections.constructor(BookCategoryResponse.class, book.category, book.count()))
                .from(book)
                .where(book.member.id.eq(memberId).and(book.createdAt.month().eq(month)))
                .groupBy(book.category)
                .orderBy(book.count().desc())
                .fetch();
    }

    @Override
    public List<BookCalendarResponse> getBookCalendarInMonth(Long memberId, int month) {
        return queryFactory
                .select(Projections.constructor(BookCalendarResponse.class, book.id, book.title, book.createdAt))
                .from(book)
                .where(book.member.id.eq(memberId).and(book.createdAt.month().eq(month)))
                .orderBy(book.createdAt.asc())
                .fetch();
    }
}
