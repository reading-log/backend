package com.api.readinglog.domain.booklog.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class BookLogsSearchByCategoryRequest {

    @Schema(description = "카테고리명 검색어")
    private String categoryName;
}
