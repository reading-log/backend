package com.api.readinglog.domain.book.controller;

import com.api.readinglog.common.response.Response;
import com.api.readinglog.common.security.CustomUserDetail;
import com.api.readinglog.domain.book.dto.BookSearchApiResponse;
import com.api.readinglog.domain.book.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping("/search")
    public Response<BookSearchApiResponse> searchBooks(@RequestParam(required = false) String query,
                                                       @RequestParam(defaultValue = "1") int start) {
        
        return Response.success(HttpStatus.OK, "책 검색 성공", bookService.searchBooks(query, start));
    }

    @PostMapping
    public Response<Void> registerBook(@AuthenticationPrincipal CustomUserDetail user) {
        // TODO: 책 등록
        return Response.success(HttpStatus.CREATED, "책 등록 성공");
    }
}
