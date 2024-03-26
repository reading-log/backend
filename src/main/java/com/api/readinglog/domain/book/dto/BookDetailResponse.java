package com.api.readinglog.domain.book.dto;

import com.api.readinglog.domain.book.entity.Book;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BookDetailResponse {

    private String title;

    private String author;

    private String publisher;

    private String category;

    private String cover;

    public static BookDetailResponse fromEntity(Book book, String cover) {
        return BookDetailResponse.builder()
                .title(book.getTitle())
                .author(book.getAuthor())
                .publisher(book.getPublisher())
                .category(book.getCategory())
                .cover(cover)
                .build();
    }

}
