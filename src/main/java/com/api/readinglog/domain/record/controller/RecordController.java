package com.api.readinglog.domain.record.controller;

import com.api.readinglog.common.response.Response;
import com.api.readinglog.common.security.CustomUserDetail;
import com.api.readinglog.domain.record.controller.dto.request.RecordModifyRequest;
import com.api.readinglog.domain.record.controller.dto.request.RecordWriteRequest;
import com.api.readinglog.domain.record.controller.dto.response.RecordResponse;
import com.api.readinglog.domain.record.service.RecordService;
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

@Tag(name = "Record", description = "독서 날짜 기록 API 목록입니다.")
@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
@Slf4j
public class RecordController {

    private final RecordService recordService;

    @Operation(summary = "독서 날짜 기록 조회", description = "특정 책에 등록한 독서 날짜 기록을 조회합니다.")
    @GetMapping("/{bookId}")
    public Response<List<RecordResponse>> getRecord(@AuthenticationPrincipal CustomUserDetail user, @PathVariable Long bookId) {

        List<RecordResponse> response = recordService.getRecord(user.getId(), bookId);
        return Response.success(HttpStatus.OK, "독서 기록 조회 성공", response);
    }

    @Operation(summary = "독서 날짜 기록 추가", description = "특정 책에 독서 날짜 기록을 추가합니다.")
    @PostMapping("/{bookId}")
    public Response<Void> addRecord(@AuthenticationPrincipal CustomUserDetail user,
                                    @PathVariable Long bookId,
                                    @RequestBody @Valid RecordWriteRequest recordWriteRequest) {

        recordService.write(user.getId(), bookId, recordWriteRequest);
        return Response.success(HttpStatus.OK, "독서 기록 추가 성공");
    }

    @Operation(summary = "독서 날짜 기록 수정", description = "등록한 독서 날짜 기록을 수정합니다.")
    @PatchMapping("/{recordId}")
    public Response<Void> modify(@AuthenticationPrincipal CustomUserDetail user,
                                 @PathVariable Long recordId,
                                 @RequestBody RecordModifyRequest recordModifyRequest) {

        recordService.modify(user.getId(), recordId, recordModifyRequest);
        return Response.success(HttpStatus.OK, "독서 기록 수정 성공");
    }

    @Operation(summary = "독서 날짜 기록 삭제", description = "등록한 독서 날짜 기록을 삭제합니다.")
    @DeleteMapping("/{recordId}")
    public Response<Void> delete(@AuthenticationPrincipal CustomUserDetail user, @PathVariable Long recordId) {

        recordService.delete(user.getId(), recordId);
        return Response.success(HttpStatus.OK, "독서 기록 삭제 성공");
    }

}
