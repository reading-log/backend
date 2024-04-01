package com.api.readinglog.domain.record.controller;

import com.api.readinglog.common.response.Response;
import com.api.readinglog.common.security.CustomUserDetail;
import com.api.readinglog.domain.record.controller.dto.RecordWriteRequest;
import com.api.readinglog.domain.record.service.RecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
@Slf4j
public class RecordController {

    private final RecordService recordService;

    @PostMapping("/{bookId}")
    public Response<Void> addRecord(@AuthenticationPrincipal CustomUserDetail user,
                                    @PathVariable Long bookId,
                                    @RequestBody @Valid RecordWriteRequest recordWriteRequest) {

        recordService.write(user.getId(), bookId, recordWriteRequest);
        return Response.success(HttpStatus.OK, "독서 기록 추가 성공");
    }
}
