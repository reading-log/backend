package com.api.readinglog.domain.summary.controller;

import com.api.readinglog.common.response.Response;
import com.api.readinglog.common.security.CustomUserDetail;
import com.api.readinglog.domain.summary.controller.dto.request.ModifyRequest;
import com.api.readinglog.domain.summary.controller.dto.request.WriteRequest;
import com.api.readinglog.domain.summary.service.SummaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Summary", description = "Summary API")
@RestController
@RequestMapping("/api/summaries")
@RequiredArgsConstructor
public class SummaryController {

    private final SummaryService summaryService;

    @Operation(summary = "Add a new summary", description = "한줄평 작성")
    @PostMapping("/{bookId}")
    public Response<Void> write(@AuthenticationPrincipal CustomUserDetail user,
                                @PathVariable Long bookId,
                                @RequestBody @Valid WriteRequest writeRequest) {

        summaryService.write(user.getId(), bookId, writeRequest);
        return Response.success(HttpStatus.CREATED, "한줄평 작성 성공");
    }

    @Operation(summary = "Modify summary", description = "한줄평 수정")
    @PatchMapping("/{summaryId}")
    public Response<Void> modify(@AuthenticationPrincipal CustomUserDetail user,
                                 @PathVariable Long summaryId,
                                 @RequestBody @Valid ModifyRequest writeRequest) {

        summaryService.modify(user.getId(), summaryId, writeRequest);
        return Response.success(HttpStatus.OK, "한줄평 수정 성공");
    }

    @Operation(summary = "Delete summary", description = "한줄평 삭제")
    @DeleteMapping("/{summaryId}")
    public Response<Void> delete(@AuthenticationPrincipal CustomUserDetail user, @PathVariable Long summaryId) {

        summaryService.delete(user.getId(), summaryId);
        return Response.success(HttpStatus.OK, "한줄평 삭제 성공");
    }

}
