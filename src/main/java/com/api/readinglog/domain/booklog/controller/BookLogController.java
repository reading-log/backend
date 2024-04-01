package com.api.readinglog.domain.booklog.controller;

import com.api.readinglog.common.response.Response;
import com.api.readinglog.common.security.CustomUserDetail;
import com.api.readinglog.domain.booklog.controller.dto.BookLogResponse;
import com.api.readinglog.domain.booklog.service.BookLogService;
import com.api.readinglog.domain.summary.controller.dto.response.SummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/book-logs")
public class BookLogController {

    private final BookLogService bookLogService;

    @GetMapping("/{bookId}/me")
    public Response<BookLogResponse> myLogs(@AuthenticationPrincipal CustomUserDetail user,
                                            @PathVariable Long bookId) {
        return Response.success(HttpStatus.OK, "나의 로그 조회 성공", bookLogService.myLogs(user.getId(), bookId));
    }

    @GetMapping
    public Response<Page<SummaryResponse>> bookLogs(@PageableDefault(sort = "createdAt", direction = Direction.DESC)
                                                    Pageable pageable) {
        // TODO: querydsl 동적 쿼리 처리
        return Response.success(HttpStatus.OK, "북로그 조회 성공", bookLogService.bookLogs(pageable));
    }
}