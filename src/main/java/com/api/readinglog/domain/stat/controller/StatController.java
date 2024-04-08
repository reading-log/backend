package com.api.readinglog.domain.stat.controller;

import com.api.readinglog.common.response.Response;
import com.api.readinglog.common.security.CustomUserDetail;
import com.api.readinglog.domain.stat.controller.dto.StatResponse;
import com.api.readinglog.domain.stat.service.StatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Stats", description = "통계 API 목록입니다.")
@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatController {

    private final StatService statService;

    @Operation(summary = "월별 통계 조회", description = "월별 통계 정보를 조회합니다.")
    @GetMapping("/{month}")
    public Response<StatResponse> getStats(@AuthenticationPrincipal CustomUserDetail user, @PathVariable("month") int month) {
        StatResponse response = statService.getStats(user.getId(), month);
        return Response.success(HttpStatus.OK, String.format("%d월 통계 조회 성공", month), response);
    }


}
