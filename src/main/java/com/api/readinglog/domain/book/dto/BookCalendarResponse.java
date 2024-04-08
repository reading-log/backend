package com.api.readinglog.domain.book.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BookCalendarResponse {

    private Long bookId;
    private String title;
    private LocalDateTime createdAt;

}
