package com.api.readinglog.domain.highlight.controller;

import com.api.readinglog.common.response.Response;
import com.api.readinglog.common.security.CustomUserDetail;
import com.api.readinglog.domain.highlight.controller.dto.request.ModifyRequest;
import com.api.readinglog.domain.highlight.controller.dto.request.WriteRequest;
import com.api.readinglog.domain.highlight.controller.dto.response.HighlightResponse;
import com.api.readinglog.domain.highlight.service.HighlightService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Tag(name = "Highlight", description = "하이라이트 API 목록입니다.")
@Slf4j
@RestController
@RequestMapping("/api/highlights")
@RequiredArgsConstructor
public class HighlightController {

    private final HighlightService highlightService;

    @Operation(summary = "내가 작성한 하이라이트 목록 조회", description = "특정 책에 등록한 하이라이트 목록을 조회합니다.")
    @GetMapping("/{bookId}/me")
    public Response<List<HighlightResponse>> highlights(@AuthenticationPrincipal CustomUserDetail user,
                                                        @PathVariable Long bookId) {

        List<HighlightResponse> response = highlightService.highlights(user.getId(), bookId);
        return Response.success(HttpStatus.OK, "내가 쓴 하이라이트 목록 조회 성공", response);
    }

    @Operation(summary = "하이라이트 작성", description = "특정 책에 하이라이트를 작성합니다.")
    @PostMapping("/{bookId}")
    public Response<Void> write(@AuthenticationPrincipal CustomUserDetail user,
                                @PathVariable Long bookId,
                                @RequestBody @Valid WriteRequest writeRequest) {

        log.debug("content: {}", writeRequest.getContent());
        highlightService.write(user.getId(), bookId, writeRequest);
        return Response.success(HttpStatus.CREATED, "하이라이트 작성 성공");
    }

    @Operation(summary = "하이라이트 수정", description = "작성한 하이라이트를 수정합니다.")
    @PatchMapping("/{highlightId}")
    public Response<Void> modify(@AuthenticationPrincipal CustomUserDetail user,
                                 @PathVariable Long highlightId,
                                 @RequestBody @Valid ModifyRequest modifyRequest) {

        highlightService.modify(user.getId(), highlightId, modifyRequest);
        return Response.success(HttpStatus.OK, "하이라이트 수정 성공");
    }

    @Operation(summary = "하이라이트 삭제", description = "작성한 하이라이트를 삭제합니다.")
    @DeleteMapping("/{highlightId}")
    public Response<Void> delete(@AuthenticationPrincipal CustomUserDetail user, @PathVariable Long highlightId) {

        highlightService.delete(user.getId(), highlightId);
        return Response.success(HttpStatus.OK, "하이라이트 삭제 성공");
    }

}
