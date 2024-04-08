package com.api.readinglog.domain.stat.service;

import com.api.readinglog.domain.book.dto.BookCalendarResponse;
import com.api.readinglog.domain.book.dto.BookCategoryResponse;
import com.api.readinglog.domain.book.repository.BookRepository;
import com.api.readinglog.domain.likesummary.repository.LikeSummaryRepository;
import com.api.readinglog.domain.stat.controller.dto.StatResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class StatService {

    private final BookRepository bookRepository;
    private final LikeSummaryRepository likeSummaryRepository;

    public StatResponse getStats(Long memberId, int month) {
        Long lastMonthTotalBookCount = bookRepository.getBookTotalCountInMonth(memberId, month - 1);
        Long thisMonthTotalBookCount = bookRepository.getBookTotalCountInMonth(memberId, month);
        List<BookCategoryResponse> bookCountGroupByCategory = bookRepository.getBookCountGroupByCategoryInMonth(memberId, month);
        List<BookCalendarResponse> bookCalendarInMonth = bookRepository.getBookCalendarInMonth(memberId, month);
        long thisMonthLikeTotalCount = likeSummaryRepository.getLikeTotalCountInMonth(memberId, month);

        return StatResponse.of(month, lastMonthTotalBookCount, thisMonthTotalBookCount, thisMonthLikeTotalCount, bookCountGroupByCategory, bookCalendarInMonth);
    }

}
