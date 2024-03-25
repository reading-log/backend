package com.api.readinglog.domain.book.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class BookDirectRequest {

    @NotBlank(message = "제목은 필수 입력 값입니다.")
    private String title;

    @NotBlank(message = "저자는 필수 입력 값입니다.")
    private String author;

    private String publisher;

    @NotBlank(message = "카테고리는 필수 입력 값입니다.")
    private String category;

    private MultipartFile cover;
}
