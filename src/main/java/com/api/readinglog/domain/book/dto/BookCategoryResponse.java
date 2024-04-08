package com.api.readinglog.domain.book.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BookCategoryResponse {

    private String category;
    private long count;
}
