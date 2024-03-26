package com.api.readinglog.domain.hightlight.controller;

import com.api.readinglog.common.response.Response;
import com.api.readinglog.common.security.CustomUserDetail;
import com.api.readinglog.domain.hightlight.controller.dto.request.ModifyRequest;
import com.api.readinglog.domain.hightlight.controller.dto.request.WriteRequest;
import com.api.readinglog.domain.hightlight.controller.dto.response.HighlightResponse;
import com.api.readinglog.domain.hightlight.service.HighlightService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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

@Slf4j
@RestController
@RequestMapping("/api/highlights")
@RequiredArgsConstructor
public class HighlightController {

    private final HighlightService highlightService;

    @GetMapping("/{bookId}/me")
    public Response<Page<HighlightResponse>> highlights(@AuthenticationPrincipal CustomUserDetail user,
                                                        @PathVariable Long bookId,
                                                        @PageableDefault(sort = "createdAt", direction = Direction.DESC) Pageable pageable) {

        Page<HighlightResponse> response = highlightService.highlights(user.getId(), bookId, pageable);
        return Response.success(HttpStatus.OK, "하이라이트 목록 조회 성공", response);
    }

    @PostMapping("/{bookId}")
    public Response<Void> write(@AuthenticationPrincipal CustomUserDetail user,
                                @PathVariable Long bookId,
                                @RequestBody @Valid WriteRequest writeRequest) {

        log.debug("content: {}", writeRequest.getContent());
        highlightService.write(user.getId(), bookId, writeRequest);
        return Response.success(HttpStatus.CREATED, "하이라이트 작성 성공");
    }

    @PatchMapping("/{highlightId}")
    public Response<Void> modify(@AuthenticationPrincipal CustomUserDetail user,
                                 @PathVariable Long highlightId,
                                 @RequestBody @Valid ModifyRequest modifyRequest) {

        highlightService.modify(user.getId(), highlightId, modifyRequest);
        return Response.success(HttpStatus.OK, "하이라이트 수정 성공");
    }

    @DeleteMapping("/{highlightId}")
    public Response<Void> delete(@AuthenticationPrincipal CustomUserDetail user, @PathVariable Long highlightId) {

        highlightService.delete(user.getId(), highlightId);
        return Response.success(HttpStatus.OK, "하이라이트 삭제 성공");
    }

}
