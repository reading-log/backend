package com.api.readinglog.domain.review.controller.dto.response;

import com.api.readinglog.domain.review.entity.Review;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewResponse {

    private String content;
    private LocalDateTime createdAt;

    public static ReviewResponse fromEntity(Review review) {
        return ReviewResponse.builder()
                .content(review.getContent())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
