package com.api.readinglog.domain.book.repository;

import static com.api.readinglog.domain.book.entity.QBook.book;

import com.api.readinglog.domain.book.dto.BookCalendarResponse;
import com.api.readinglog.domain.book.dto.BookCategoryResponse;
import com.api.readinglog.domain.book.dto.BookResponse;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@RequiredArgsConstructor
public class CustomBookRepositoryImpl implements CustomBookRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<BookResponse> myBookList(Long memberId, String keyword, Pageable pageable) {

        List<BookResponse> response = queryFactory.select(Projections.constructor(
                        BookResponse.class, book.id, book.member.id, book.cover, book.title, book.author, book.createdAt)
                )
                .from(book)
                .where(book.member.id.eq(memberId), containKeyword(keyword))
                .orderBy(getAllOrderSpecifiers(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory.select(book.count())
                .from(book)
                .where(book.member.id.eq(memberId), containKeyword(keyword))
                .fetchFirst();

        return new PageImpl<>(response, pageable, count);
    }

    private BooleanExpression containKeyword(String keyword) {
        if(keyword == null || keyword.isEmpty()) {
            return null;
        }
        return book.title.containsIgnoreCase(keyword);
    }

    @Override
    public Long getBookTotalCountInMonth(Long memberId, int month) {
        return queryFactory
                .select(book.count())
                .from(book)
                .where(book.member.id.eq(memberId).and(book.createdAt.month().eq(month)))
                .fetchFirst();
    }

    @Override
    public List<BookCategoryResponse> getBookCountGroupByCategoryInMonth(Long memberId, int month) {
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

    private OrderSpecifier<?>[] getAllOrderSpecifiers(Pageable pageable) {
        List<OrderSpecifier<?>> orderSpecifierList = new ArrayList<>();

        for (Sort.Order order : pageable.getSort()) {
            Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;

            switch (order.getProperty()) {
                case "createdAt" -> orderSpecifierList.add(new OrderSpecifier<>(direction, book.createdAt));
            }
        }

        return orderSpecifierList.toArray(OrderSpecifier[]::new);
    }
}
