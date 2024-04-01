package com.api.readinglog.domain.booklog.controller.dto;

import com.api.readinglog.domain.book.dto.BookDetailResponse;
import com.api.readinglog.domain.highlight.controller.dto.response.HighlightResponse;
import com.api.readinglog.domain.review.controller.dto.response.ReviewResponse;
import com.api.readinglog.domain.summary.controller.dto.response.MySummaryResponse;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookLogResponse {

    private final BookDetailResponse bookInfo;
    private final MySummaryResponse summary;
    private final List<ReviewResponse> reviews;
    private final List<HighlightResponse> highlights;

    public static BookLogResponse of(BookDetailResponse bookInfo, MySummaryResponse summary,
                                     List<ReviewResponse> reviews, List<HighlightResponse> highlights) {
        return BookLogResponse.builder()
                .bookInfo(bookInfo)
                .summary(summary)
                .reviews(reviews)
                .highlights(highlights)
                .build();
    }
}
