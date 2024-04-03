package com.api.readinglog.domain.booklog.controller;

import com.api.readinglog.common.response.Response;
import com.api.readinglog.common.security.CustomUserDetail;
import com.api.readinglog.domain.booklog.controller.dto.BookLogResponse;
import com.api.readinglog.domain.booklog.service.BookLogService;
import com.api.readinglog.domain.summary.controller.dto.response.SummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "BookLogs", description = "북로그 API 목록입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/book-logs")
public class BookLogController {

    private final BookLogService bookLogService;

    @Operation(summary = "나의 로그 조회", description = "인증 토큰을 통해 특정 사용자가 작성한 로그 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "나의 로그 조회 성공",
                    content = {@Content(schema = @Schema(implementation = Response.class))}),
            @ApiResponse(responseCode = "400", description = "나의 로그 조회 실패")
    })
    @GetMapping("/{bookId}/me")
    public Response<BookLogResponse> myLogs(@AuthenticationPrincipal CustomUserDetail user,
                                            @PathVariable Long bookId) {
        return Response.success(HttpStatus.OK, "나의 로그 조회 성공", bookLogService.myLogs(user.getId(), bookId));
    }

    @Operation(summary = "북로그 조회", description = "리딩 로그 서비스의 모든 북로그를 조회합니다. 비회원도 조회가 가능합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "북로그 조회 성공",
                    content = {@Content(schema = @Schema(implementation = Response.class))}),
            @ApiResponse(responseCode = "404", description = "북로그 목록이 존재하지 않습니다!")
    })
    @GetMapping
    public Response<Page<SummaryResponse>> bookLogs(@PageableDefault(sort = "createdAt", direction = Direction.DESC)
                                                    Pageable pageable) {
        // TODO: querydsl 동적 쿼리 처리
        return Response.success(HttpStatus.OK, "북로그 조회 성공", bookLogService.bookLogs(pageable));
    }
}