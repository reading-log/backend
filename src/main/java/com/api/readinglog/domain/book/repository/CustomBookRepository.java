package com.api.readinglog.domain.book.repository;

import com.api.readinglog.domain.book.dto.BookCalendarResponse;
import com.api.readinglog.domain.book.dto.BookCategoryResponse;
import com.api.readinglog.domain.book.dto.BookResponse;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomBookRepository {

    Page<BookResponse> myBookList(Long memberId, String keyword, Pageable pageable);

    Long getBookTotalCountInMonth(Long memberId, int month);

    List<BookCategoryResponse> getBookCountGroupByCategoryInMonth(Long memberId, int month);

    List<BookCalendarResponse> getBookCalendarInMonth(Long memberId, int month);
}
