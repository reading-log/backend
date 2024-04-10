package com.api.readinglog.domain.booklog.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class BookLogsSearchByBookTitleRequest {

    @Schema(description = "책 제목 검색어")
    private String bookTitle;

}
