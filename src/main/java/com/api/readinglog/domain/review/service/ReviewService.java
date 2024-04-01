package com.api.readinglog.domain.review.service;

import com.api.readinglog.common.exception.ErrorCode;
import com.api.readinglog.common.exception.custom.ReviewException;
import com.api.readinglog.domain.book.entity.Book;
import com.api.readinglog.domain.book.service.BookService;
import com.api.readinglog.domain.member.entity.Member;
import com.api.readinglog.domain.member.service.MemberService;
import com.api.readinglog.domain.review.controller.dto.request.ModifyRequest;
import com.api.readinglog.domain.review.controller.dto.request.WriteRequest;
import com.api.readinglog.domain.review.controller.dto.response.ReviewResponse;
import com.api.readinglog.domain.review.entity.Review;
import com.api.readinglog.domain.review.repository.ReviewRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MemberService memberService;
    private final BookService bookService;

    @Transactional(readOnly = true)
    public List<ReviewResponse> reviews(Long memberId, Long bookId) {
        Member member = memberService.getMemberById(memberId);
        Book book = bookService.getBookById(bookId);

        List<ReviewResponse> reviews = reviewRepository.findAllByMemberAndBook(member, book)
                .stream()
                .map(ReviewResponse::fromEntity)
                .collect(Collectors.toList());

        if (reviews.isEmpty()) {
            throw new ReviewException(ErrorCode.NOT_FOUND_REVIEW);
        }

        return reviews;
    }

    public void write(Long memberId, Long bookId, WriteRequest request) {
        Member member = memberService.getMemberById(memberId);
        Book book = bookService.getBookById(bookId);

        Review review = Review.of(member, book, request);
        reviewRepository.save(review);
    }

    public void modify(Long memberId, Long reviewId, ModifyRequest request) {
        Member member = memberService.getMemberById(memberId);
        Review review = getReviewById(reviewId);

        if (review.getMember() != member) {
            throw new ReviewException(ErrorCode.FORBIDDEN_MODIFY);
        }

        review.modify(request);
    }

    public void delete(Long memberId, Long reviewId) {
        Member member = memberService.getMemberById(memberId);
        Review review = getReviewById(reviewId);

        if (review.getMember() != member) {
            throw new ReviewException(ErrorCode.FORBIDDEN_DELETE);
        }

        reviewRepository.delete(review);
    }

    public Review getReviewById(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewException(ErrorCode.NOT_FOUND_REVIEW));
    }

}
