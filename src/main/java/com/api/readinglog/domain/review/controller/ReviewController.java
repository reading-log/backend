package com.api.readinglog.domain.review.controller;

import com.api.readinglog.common.response.Response;
import com.api.readinglog.common.security.CustomUserDetail;
import com.api.readinglog.domain.review.controller.dto.request.ModifyRequest;
import com.api.readinglog.domain.review.controller.dto.request.WriteRequest;
import com.api.readinglog.domain.review.controller.dto.response.ReviewResponse;
import com.api.readinglog.domain.review.service.ReviewService;
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

@Tag(name = "Review", description = "서평 API 목록입니다.")
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "내가 작성한 서평 목록 조회", description = "특정 책에 등록한 서평 목록을 조회합니다.")
    @GetMapping("/{bookId}/me")
    public Response<List<ReviewResponse>> reviews(@AuthenticationPrincipal CustomUserDetail user,
                                                  @PathVariable Long bookId) {

        List<ReviewResponse> response = reviewService.reviews(user.getId(), bookId);
        return Response.success(HttpStatus.OK, "내가 쓴 서평 목록 조회 성공", response);
    }

    @Operation(summary = "서평 작성", description = "특정 책에 서평을 작성합니다.")
    @PostMapping("/{bookId}")
    public Response<Void> write(@AuthenticationPrincipal CustomUserDetail user,
                                @PathVariable Long bookId,
                                @RequestBody @Valid WriteRequest writeRequest) {

        reviewService.write(user.getId(), bookId, writeRequest);
        return Response.success(HttpStatus.CREATED, "서평 작성 성공");
    }

    @Operation(summary = "서평 수정", description = "작성한 서평을 수정합니다.")
    @PatchMapping("/{reviewId}")
    public Response<Void> modify(@AuthenticationPrincipal CustomUserDetail user,
                                 @PathVariable Long reviewId,
                                 @RequestBody @Valid ModifyRequest modifyRequest) {

        reviewService.modify(user.getId(), reviewId, modifyRequest);
        return Response.success(HttpStatus.OK, "서평 수정 성공");
    }

    @Operation(summary = "서평 삭제", description = "작성한 서평을 삭제합니다.")
    @DeleteMapping("/{reviewId}")
    public Response<Void> modify(@AuthenticationPrincipal CustomUserDetail user, @PathVariable Long reviewId) {

        reviewService.delete(user.getId(), reviewId);
        return Response.success(HttpStatus.OK, "서평 삭제 성공");
    }

}
