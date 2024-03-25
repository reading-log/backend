package com.api.readinglog.domain.book.controller;

import com.api.readinglog.common.response.Response;
import com.api.readinglog.common.security.CustomUserDetail;
import com.api.readinglog.domain.book.dto.BookDirectRequest;
import com.api.readinglog.domain.book.dto.BookRegisterRequest;
import com.api.readinglog.domain.book.dto.BookSearchApiResponse;
import com.api.readinglog.domain.book.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Response<Void> registerBookAfterSearch(@AuthenticationPrincipal CustomUserDetail user,
                                       @RequestBody @Valid BookRegisterRequest request) {
        bookService.registerBookAfterSearch(user.getId(), request);
        return Response.success(HttpStatus.CREATED, "책 등록 성공");
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Response<Void> registerBookDirect(@AuthenticationPrincipal CustomUserDetail user,
                                             @ModelAttribute @Valid BookDirectRequest request) {
        bookService.registerBookDirect(user.getId(), request);
        return Response.success(HttpStatus.CREATED, "책 등록 성공");
    }

    @DeleteMapping("/{bookId}")
    public Response<Void> deleteBook(@AuthenticationPrincipal CustomUserDetail user, @PathVariable Long bookId) {
        // TODO: 책이 삭제될 때 해당 책에 관한 기록, 포스트 함께 삭제?
        bookService.deleteBook(user.getId(), bookId);
        return Response.success(HttpStatus.OK, "%d번 책 삭제 성공".formatted(bookId));
    }
}
