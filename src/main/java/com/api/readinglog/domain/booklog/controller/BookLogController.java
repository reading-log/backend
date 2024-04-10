package com.api.readinglog.domain.booklog.controller;

import com.api.readinglog.common.response.Response;
import com.api.readinglog.common.security.CustomUserDetail;
import com.api.readinglog.domain.booklog.controller.dto.BookLogResponse;
import com.api.readinglog.domain.booklog.controller.dto.request.BookLogsSearchByBookTitleRequest;
import com.api.readinglog.domain.booklog.controller.dto.request.BookLogsSearchByCategoryRequest;
import com.api.readinglog.domain.booklog.service.BookLogService;
import com.api.readinglog.domain.summary.controller.dto.response.SummaryPageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "BookLogs", description = "북로그 API 목록입니다.")
@RestController
@RequiredArgsConstructor
@Slf4j
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
        return Response.success(HttpStatus.OK, "나의 로그 조회 성공", bookLogService.bookLogDetails(user.getId(), bookId));
    }

    @Operation(summary = "북로그 목록 조회", description = "리딩 로그 서비스의 전체 북로그 목록을 조회합니다. 기본값은 최신순 정렬입니다. 정렬 조건을 추가하면 인기순 정렬이 가능합니다. 비회원도 조회가 가능합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "북로그 목록 조회 성공",
                    content = {@Content(schema = @Schema(implementation = Response.class))}),
            @ApiResponse(responseCode = "404", description = "북로그 목록이 존재하지 않습니다!")
    })
    @GetMapping
    public Response<SummaryPageResponse> bookLogs(
            @PageableDefault(sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
        return Response.success(HttpStatus.OK, "북로그 목록 조회 성공", bookLogService.bookLogs(pageable));
    }

    @Operation(summary = "북로그 상세 조회", description = "북로그 상세 조회입니다. 회원과 책 ID값을 통해 조회할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "북로그 상세 조회 성공",
                    content = {@Content(schema = @Schema(implementation = Response.class))}),
            @ApiResponse(responseCode = "400", description = "북로그 상세 조회 실패")
    })
    @GetMapping("/{bookId}/{memberId}")
    public Response<BookLogResponse> bookLogsDetails(@PathVariable Long bookId,
                                                     @PathVariable Long memberId) {
        return Response.success(HttpStatus.OK, "북로그 상세 조회 성공", bookLogService.bookLogDetails(memberId, bookId));
    }

    @Operation(summary = "북로그 책 제목으로 검색", description = "책 제목을 통해 북로그 목록을 조회합니다. 기본값은 최신순 정렬입니다. 정렬 조건을 추가하면 인기순 정렬이 가능합니다. 비회원도 조회가 가능합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "북로그 책 제목 검색 성공",
                    content = {@Content(schema = @Schema(implementation = Response.class))}),
            @ApiResponse(responseCode = "404", description = "북로그 목록이 존재하지 않습니다!")
    })
    @GetMapping("/search/title")
    public Response<SummaryPageResponse> findBookLogsByBookTitle(@RequestBody BookLogsSearchByBookTitleRequest request,
                                                                 @PageableDefault(sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
        return Response.success(HttpStatus.OK, "북로그 책 제목 검색 성공",
                bookLogService.findBookLogsByBookTitle(request, pageable));
    }

    @Operation(summary = "북로그 책 카테고리명으로 검색", description = "책 카테고리명을 통해 북로그 목록을 조회합니다. 기본값은 최신순 정렬입니다. 정렬 조건을 추가하면 인기순 정렬이 가능합니다. 비회원도 조회가 가능합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "북로그 책 카테고리명 검색 성공",
                    content = {@Content(schema = @Schema(implementation = Response.class))}),
            @ApiResponse(responseCode = "404", description = "북로그 목록이 존재하지 않습니다!")
    })
    @GetMapping("/search/category")
    public Response<SummaryPageResponse> findBookLogsByCategory(@RequestBody BookLogsSearchByCategoryRequest request,
                                                                @PageableDefault(sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
        return Response.success(HttpStatus.OK, "북로그 책 카테고리명 검색 성공",
                bookLogService.findBookLogsByCategory(request, pageable));
    }
}