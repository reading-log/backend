package com.api.readinglog.domain.summary.controller;

import com.api.readinglog.common.response.Response;
import com.api.readinglog.common.security.CustomUserDetail;
import com.api.readinglog.domain.likesummary.service.LikeSummaryService;
import com.api.readinglog.domain.summary.controller.dto.request.ModifyRequest;
import com.api.readinglog.domain.summary.controller.dto.request.WriteRequest;
import com.api.readinglog.domain.summary.controller.dto.response.MySummaryResponse;
import com.api.readinglog.domain.summary.service.SummaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
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

@Tag(name = "Summary", description = "한줄평 API 목록입니다.")
@RestController
@RequestMapping("/api/summaries")
@RequiredArgsConstructor
public class SummaryController {

    private final SummaryService summaryService;
    private final LikeSummaryService likeSummaryService;

    @Operation(summary = "내가 작성한 한줄평 목록 조회", description = "특정 책에 등록한 한줄평 목록을 조회합니다.")
    @GetMapping("/{bookId}/me")
    public Response<List<MySummaryResponse>> summaries(@AuthenticationPrincipal CustomUserDetail user,
                                                       @PathVariable Long bookId) {
        List<MySummaryResponse> response = summaryService.summaries(user.getId(), bookId);
        return Response.success(HttpStatus.OK, "내가 쓴 한줄평 목록 조회 성공", response);
    }


    @Operation(summary = "한줄평 작성", description = "특정 책에 한줄평을 등록합니다.")
    @PostMapping("/{bookId}")
    public Response<Void> write(@AuthenticationPrincipal CustomUserDetail user,
                                @PathVariable Long bookId,
                                @RequestBody @Valid WriteRequest writeRequest) {

        summaryService.write(user.getId(), bookId, writeRequest);
        return Response.success(HttpStatus.CREATED, "한줄평 작성 성공");
    }

    @Operation(summary = "한줄평 수정", description = "작성한 한줄평을 수정합니다.")
    @PatchMapping("/{summaryId}")
    public Response<Void> modify(@AuthenticationPrincipal CustomUserDetail user,
                                 @PathVariable Long summaryId,
                                 @RequestBody @Valid ModifyRequest writeRequest) {

        summaryService.modify(user.getId(), summaryId, writeRequest);
        return Response.success(HttpStatus.OK, "한줄평 수정 성공");
    }

    @Operation(summary = "한줄평 삭제", description = "작성한 한줄평을 삭제합니다.")
    @DeleteMapping("/{summaryId}")
    public Response<Void> delete(@AuthenticationPrincipal CustomUserDetail user, @PathVariable Long summaryId) {

        summaryService.delete(user.getId(), summaryId);
        return Response.success(HttpStatus.OK, "한줄평 삭제 성공");
    }

    @Operation(summary = "한 줄평 좋아요 등록", description = "한 줄평 좋아요 등록입니다.")
    @PostMapping("/likes/{summaryId}")
    public Response<Void> like(@AuthenticationPrincipal CustomUserDetail user,
                               @PathVariable Long summaryId) {
        likeSummaryService.addLikeSummary(user.getId(), summaryId);
        return Response.success(HttpStatus.OK, "한 줄평 좋아요 등록 성공");
    }

    @Operation(summary = "한 줄평 좋아요 취소", description = "한 줄평 좋아요 취소입니다.")
    @DeleteMapping("/likes/{summaryId}")
    public Response<Void> unlike(@AuthenticationPrincipal CustomUserDetail user,
                                 @PathVariable Long summaryId) {
        likeSummaryService.deleteLikeSummary(user.getId(), summaryId);
        return Response.success(HttpStatus.OK, "한 줄평 좋아요 취소 성공");
    }
}
