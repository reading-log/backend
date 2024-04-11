package com.api.readinglog.domain.book.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BookResponse {

    private Long bookId;
    private Long memberId;
    private String cover;
    private String title;
    private String author;
    private LocalDateTime createdAt;

}
