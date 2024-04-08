package com.api.readinglog.domain.summary.controller.dto.response;

import com.api.readinglog.domain.summary.entity.Summary;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SummaryResponse {

    private String nickname;
    private String bookTitle;
    private String bookAuthor;
    private String bookCover;
    private String content;
    private LocalDateTime createdAt;
    private int likeCount;

    public static SummaryResponse fromEntity(Summary summary, int likeCount) {
        return SummaryResponse.builder()
                .nickname(summary.getMember().getNickname())
                .bookTitle(summary.getBook().getTitle())
                .bookAuthor(summary.getBook().getAuthor())
                .bookCover(summary.getBook().getCover())
                .content(summary.getContent())
                .createdAt(summary.getCreatedAt())
                .likeCount(likeCount)
                .build();
    }
}
