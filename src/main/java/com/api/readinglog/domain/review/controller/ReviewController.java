package com.api.readinglog.domain.review.controller;

import com.api.readinglog.common.response.Response;
import com.api.readinglog.common.security.CustomUserDetail;
import com.api.readinglog.domain.review.controller.dto.request.ModifyRequest;
import com.api.readinglog.domain.review.controller.dto.request.WriteRequest;
import com.api.readinglog.domain.review.controller.dto.response.ReviewResponse;
import com.api.readinglog.domain.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/{bookId}/me")
    public Response<Page<ReviewResponse>> reviews(@AuthenticationPrincipal CustomUserDetail user,
                                                  @PathVariable Long bookId,
                                                  @PageableDefault(sort = "createdAt", direction = Direction.DESC) Pageable pageable) {

        Page<ReviewResponse> response = reviewService.reviews(user.getId(), bookId, pageable);
        return Response.success(HttpStatus.OK, "내가 쓴 서평 목록 조회 성공", response);
    }

    @PostMapping("/{bookId}")
    public Response<Void> write(@AuthenticationPrincipal CustomUserDetail user,
                                @PathVariable Long bookId,
                                @RequestBody @Valid WriteRequest writeRequest) {

        reviewService.write(user.getId(), bookId, writeRequest);
        return Response.success(HttpStatus.CREATED, "서평 작성 성공");
    }

    @PatchMapping("/{reviewId}")
    public Response<Void> modify(@AuthenticationPrincipal CustomUserDetail user,
                                 @PathVariable Long reviewId,
                                 @RequestBody @Valid ModifyRequest modifyRequest) {

        reviewService.modify(user.getId(), reviewId, modifyRequest);
        return Response.success(HttpStatus.OK, "서평 수정 성공");
    }

    @DeleteMapping("/{reviewId}")
    public Response<Void> modify(@AuthenticationPrincipal CustomUserDetail user, @PathVariable Long reviewId) {

        reviewService.delete(user.getId(), reviewId);
        return Response.success(HttpStatus.OK, "서평 삭제 성공");
    }

}
