package com.api.readinglog.domain.summary.controller.dto.response;

import com.api.readinglog.domain.summary.entity.Summary;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MySummaryResponse {

    private Long id;
    private String nickname;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static MySummaryResponse fromEntity(Summary summary) {
        return MySummaryResponse.builder()
                .id(summary.getId())
                .nickname(summary.getMember().getNickname())
                .content(summary.getContent())
                .createdAt(summary.getCreatedAt())
                .modifiedAt(summary.getModifiedAt())
                .build();
    }
}
