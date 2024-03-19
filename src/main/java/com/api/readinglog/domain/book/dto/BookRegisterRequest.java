package com.api.readinglog.domain.book.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookRegisterRequest {

    private Integer itemId;
    private String title;
    private String author;
    private String publisher;
    private String category;
    private String cover;
}
