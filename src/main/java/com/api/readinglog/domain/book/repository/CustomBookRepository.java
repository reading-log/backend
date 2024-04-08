package com.api.readinglog.domain.book.repository;

import com.api.readinglog.domain.book.dto.BookCalendarResponse;
import com.api.readinglog.domain.book.dto.BookCategoryResponse;
import java.util.List;

public interface CustomBookRepository {

    Long getBookTotalCountInMonth(long memberId, int month);

    List<BookCategoryResponse> getBookCountGroupByCategory(Long memberId, int month);

    List<BookCalendarResponse> getBookCalendarInMonth(Long memberId, int month);
}
