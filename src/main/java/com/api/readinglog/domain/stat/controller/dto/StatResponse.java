package com.api.readinglog.domain.stat.controller.dto;

import com.api.readinglog.domain.book.dto.BookCalendarResponse;
import com.api.readinglog.domain.book.dto.BookCategoryResponse;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StatResponse {

    private int month; // 월
    private Long lastMonthTotalBookCount; // 지난달 읽은 책의 총 권수
    private Long thisMonthTotalBookCount; // 이번달 읽은 책의 총 권수
    private Long thisMonthLikeCount; // 이번달 받은 좋아요의 총 개수
    private List<BookCategoryResponse> bookCountGroupByCategory; // 카테고리별 읽은 책 권수
    private List<BookCalendarResponse> bookCalendarList; // 등록한 책 목록


    public static StatResponse of(int month, Long lastMonthTotalBookCount, Long thisMonthTotalBookCount, Long thisMonthLikeCount, List<BookCategoryResponse> bookCountGroupByCategory, List<BookCalendarResponse> bookCalendarList) {
        return StatResponse.builder()
                .month(month)
                .lastMonthTotalBookCount(lastMonthTotalBookCount)
                .thisMonthTotalBookCount(thisMonthTotalBookCount)
                .thisMonthLikeCount(thisMonthLikeCount)
                .bookCountGroupByCategory(bookCountGroupByCategory)
                .bookCalendarList(bookCalendarList)
                .build();
    }

}
