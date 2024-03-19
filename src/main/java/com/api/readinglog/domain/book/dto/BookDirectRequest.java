package com.api.readinglog.domain.book.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class BookDirectRequest {

    private String title;
    private String author;
    private String publisher;
    private String category;
    private MultipartFile cover;
}
