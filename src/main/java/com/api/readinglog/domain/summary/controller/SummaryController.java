package com.api.readinglog.domain.summary.controller;

import com.api.readinglog.common.response.Response;
import com.api.readinglog.common.security.CustomUserDetail;
import com.api.readinglog.domain.summary.controller.dto.request.ModifyRequest;
import com.api.readinglog.domain.summary.controller.dto.request.WriteRequest;
import com.api.readinglog.domain.summary.controller.dto.response.SummaryResponse;
import com.api.readinglog.domain.summary.controller.dto.response.MySummaryResponse;
import com.api.readinglog.domain.summary.service.SummaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/summaries")
@RequiredArgsConstructor
public class SummaryController {

    private final SummaryService summaryService;

    @GetMapping("/feed")
    public Response<Page<SummaryResponse>> feed(@PageableDefault(sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
        // TODO: querydsl 동적 쿼리 처리
        return Response.success(HttpStatus.OK, "피드 목록 조회 성공", summaryService.feed(pageable));
    }

    @GetMapping("/{bookId}/me")
    public Response<MySummaryResponse> mySummary(@AuthenticationPrincipal CustomUserDetail user,
                                                 @PathVariable Long bookId) {

        MySummaryResponse response = summaryService.mySummary(user.getId(), bookId);
        return Response.success(HttpStatus.OK, "내 한줄평 조회 성공", response);
    }

    @PostMapping("/{bookId}")
    public Response<Void> write(@AuthenticationPrincipal CustomUserDetail user,
                                @PathVariable Long bookId,
                                @RequestBody @Valid WriteRequest writeRequest) {

        summaryService.write(user.getId(), bookId, writeRequest);
        return Response.success(HttpStatus.CREATED, "한줄평 작성 성공");
    }

    @PatchMapping("/{summaryId}")
    public Response<Void> modify(@AuthenticationPrincipal CustomUserDetail user,
                                 @PathVariable Long summaryId,
                                 @RequestBody @Valid ModifyRequest writeRequest) {

        summaryService.modify(user.getId(), summaryId, writeRequest);
        return Response.success(HttpStatus.OK, "한줄평 수정 성공");
    }

    @DeleteMapping("/{summaryId}")
    public Response<Void> delete(@AuthenticationPrincipal CustomUserDetail user, @PathVariable Long summaryId) {

        summaryService.delete(user.getId(), summaryId);
        return Response.success(HttpStatus.OK, "한줄평 삭제 성공");
    }

}
