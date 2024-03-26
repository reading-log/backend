package com.api.readinglog.domain.summary.controller.dto.response;

import com.api.readinglog.domain.summary.entity.Summary;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MySummaryResponse {

    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static MySummaryResponse fromEntity(Summary summary) {
        return MySummaryResponse.builder()
                .content(summary.getContent())
                .createdAt(summary.getCreatedAt())
                .modifiedAt(summary.getModifiedAt())
                .build();
    }
}
