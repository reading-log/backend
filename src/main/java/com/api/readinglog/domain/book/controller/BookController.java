package com.api.readinglog.domain.book.controller;

import static org.springframework.data.domain.Sort.Direction.DESC;

import com.api.readinglog.common.response.Response;
import com.api.readinglog.common.security.CustomUserDetail;
import com.api.readinglog.domain.book.dto.BookDetailResponse;
import com.api.readinglog.domain.book.dto.BookDirectRequest;
import com.api.readinglog.domain.book.dto.BookRegisterRequest;
import com.api.readinglog.domain.book.dto.BookResponse;
import com.api.readinglog.domain.book.dto.BookSearchApiResponse;
import com.api.readinglog.domain.book.dto.BookModifyRequest;
import com.api.readinglog.domain.book.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Book", description = "책 API 목록입니다.")
@Slf4j
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping("/me")
    public Response<Page<BookResponse>> myBookList(@AuthenticationPrincipal CustomUserDetail user,
                                     @RequestParam(required = false) String keyword,
                                     @PageableDefault(sort = "createdAt", direction = DESC) Pageable pageable) {

        Page<BookResponse> response = bookService.myBookList(user.getId(), keyword, pageable);
        return Response.success(HttpStatus.OK, "내가 등록한 책 목록 조회 성공", response);
    }

    @Operation(summary = "등록한 책 조회", description = "사용자가 등록한 책 정보를 조회합니다.")
    @GetMapping("/{bookId}")
    public Response<BookDetailResponse> getBookInfo(@AuthenticationPrincipal CustomUserDetail user, @PathVariable Long bookId) {

        return Response.success(HttpStatus.OK, String.format("%d번 책 정보 응답 성공", bookId), bookService.getBookInfo(user.getId(), bookId));
    }

    @Operation(summary = "책 검색", description = "검색어를 통해 책을 검색합니다.")
    @GetMapping("/search")
    public Response<BookSearchApiResponse> searchBooks(@RequestParam(required = false) String q,
                                                       @RequestParam(defaultValue = "1") int start) {
        
        return Response.success(HttpStatus.OK, "책 검색 결과 조회", bookService.searchBooks(q, start));
    }

    @Operation(summary = "책 검색 후 등록", description = "검색한 책 정보를 통해 책을 등록합니다.")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Response<Void> registerBookAfterSearch(@AuthenticationPrincipal CustomUserDetail user,
                                       @RequestBody @Valid BookRegisterRequest request) {

        bookService.registerBookAfterSearch(user.getId(), request);
        return Response.success(HttpStatus.CREATED, "책 등록 성공");
    }

    @Operation(summary = "책 직접 등록", description = "책 정보를 직접 입력해서 책을 등록합니다.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Response<Void> registerBookDirect(@AuthenticationPrincipal CustomUserDetail user,
                                             @ModelAttribute @Valid BookDirectRequest request) {

        bookService.registerBookDirect(user.getId(), request);
        return Response.success(HttpStatus.CREATED, "책 등록 성공");
    }

    @Operation(summary = "책 정보 수정", description = "등록한 책 정보를 수정합니다.")
    @PatchMapping("/{bookId}")
    public Response<Void> modifyBook(@AuthenticationPrincipal CustomUserDetail user,
                                     @ModelAttribute BookModifyRequest bookModifyRequest,
                                     @PathVariable Long bookId) {

        bookService.modifyBook(user.getId(), bookId, bookModifyRequest);
        return Response.success(HttpStatus.OK, "책 수정 성공");
    }

    @Operation(summary = "책 삭제", description = "등록한 책을 삭제합니다.")
    @DeleteMapping("/{bookId}")
    public Response<Void> deleteBook(@AuthenticationPrincipal CustomUserDetail user, @PathVariable Long bookId) {
        bookService.deleteBook(user.getId(), bookId);
        return Response.success(HttpStatus.OK, "%d번 책 삭제 성공".formatted(bookId));
    }
}
