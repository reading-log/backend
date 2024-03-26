package com.api.readinglog.domain.review.service;

import com.api.readinglog.domain.book.entity.Book;
import com.api.readinglog.domain.book.service.BookService;
import com.api.readinglog.domain.member.entity.Member;
import com.api.readinglog.domain.member.service.MemberService;
import com.api.readinglog.domain.review.controller.dto.request.WriteRequest;
import com.api.readinglog.domain.review.entity.Review;
import com.api.readinglog.domain.review.repository.ReviewRepository;
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

    public void write(Long memberId, Long bookId, WriteRequest writeRequest) {
        Member member = memberService.getMemberById(memberId);
        Book book = bookService.getBookById(bookId);

        Review review = Review.of(member, book, writeRequest);
        reviewRepository.save(review);
    }
}
