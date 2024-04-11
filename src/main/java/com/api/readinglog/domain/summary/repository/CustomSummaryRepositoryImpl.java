package com.api.readinglog.domain.summary.repository;

import static com.api.readinglog.domain.book.entity.QBook.book;
import static com.api.readinglog.domain.summary.entity.QSummary.summary;

import com.api.readinglog.domain.summary.entity.Summary;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class CustomSummaryRepositoryImpl implements CustomSummaryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Summary> findByBookTitle(String bookTitle, Pageable pageable) {
        return queryFactory
                .select(summary)
                .from(summary)
                .join(summary.book, book)
                .where(book.title.contains(bookTitle)) // 검색어를 포함
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1) // 다음 페이지 존재 여부 판별
                .fetch();
    }

    @Override
    public List<Summary> findByCategoryName(String categoryTitle, Pageable pageable) {
        return queryFactory
                .select(summary)
                .from(summary)
                .join(summary.book, book)
                .where(book.category.eq(categoryTitle)) // 검색어와 일치
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1) // 다음 페이지 존재 여부 판별
                .fetch();
    }
}
