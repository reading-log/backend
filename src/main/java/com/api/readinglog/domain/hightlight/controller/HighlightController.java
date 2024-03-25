package com.api.readinglog.domain.hightlight.controller;

import com.api.readinglog.common.response.Response;
import com.api.readinglog.common.security.CustomUserDetail;
import com.api.readinglog.domain.hightlight.controller.dto.WriteRequest;
import com.api.readinglog.domain.hightlight.service.HighlightService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.protocol.HTTP;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @PostMapping("/{bookId}")
    public Response<Void> write(@AuthenticationPrincipal CustomUserDetail user,
                                @PathVariable Long bookId,
                                @RequestBody @Valid WriteRequest writeRequest) {

        log.debug("content: {}", writeRequest.getContent());
        highlightService.write(user.getId(), bookId, writeRequest);
        return Response.success(HttpStatus.CREATED, "하이라이트 작성 성공");
    }

}
