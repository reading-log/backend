package com.api.readinglog.domain.review.service;

import com.api.readinglog.common.exception.ErrorCode;
import com.api.readinglog.common.exception.custom.ReviewException;
import com.api.readinglog.common.exception.custom.SummaryException;
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
                .toList();

        return reviews;
    }

    public void write(Long memberId, Long bookId, WriteRequest request) {
        Member member = memberService.getMemberById(memberId);
        Book book = bookService.getBookById(bookId);

        // 해당 책에 대한 서평이 존재하는 경우 예외 처리
        reviewRepository.findByMemberAndBook(member, book).ifPresent(it -> {
            throw new ReviewException(ErrorCode.REVIEW_ALREADY_EXISTS);
        });

        Review review = reviewRepository.save(Review.of(member, book, request));
        book.getReviewList().add(review);
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
