package com.api.readinglog.domain.record.controller;

import com.api.readinglog.common.response.Response;
import com.api.readinglog.common.security.CustomUserDetail;
import com.api.readinglog.domain.record.controller.dto.request.RecordWriteRequest;
import com.api.readinglog.domain.record.controller.dto.response.RecordResponse;
import com.api.readinglog.domain.record.service.RecordService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/{bookId}")
    public Response<List<RecordResponse>> getRecord(@AuthenticationPrincipal CustomUserDetail user, @PathVariable Long bookId) {

        List<RecordResponse> response = recordService.getRecord(user.getId(), bookId);
        return Response.success(HttpStatus.OK, "독서 기록 조회 성공", response);
    }
}
