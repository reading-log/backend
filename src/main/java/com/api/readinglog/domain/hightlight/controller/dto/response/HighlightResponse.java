package com.api.readinglog.domain.hightlight.controller.dto.response;

import com.api.readinglog.domain.hightlight.entity.Highlight;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HighlightResponse {

    private String content;
    private Integer page;
    private LocalDateTime createdAt;

    public static HighlightResponse fromEntity(Highlight highlight) {
        return HighlightResponse.builder()
                .content(highlight.getContent())
                .page(highlight.getPage())
                .createdAt(highlight.getCreatedAt())
                .build();
    }

}
