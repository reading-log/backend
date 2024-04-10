package com.api.readinglog.domain.booklog.service;

import com.api.readinglog.common.exception.ErrorCode;
import com.api.readinglog.common.exception.custom.SummaryException;
import com.api.readinglog.domain.book.dto.BookDetailResponse;
import com.api.readinglog.domain.book.entity.Book;
import com.api.readinglog.domain.book.service.BookService;
import com.api.readinglog.domain.booklog.controller.dto.BookLogResponse;
import com.api.readinglog.domain.highlight.controller.dto.response.HighlightResponse;
import com.api.readinglog.domain.highlight.repository.HighlightRepository;
import com.api.readinglog.domain.likesummary.service.LikeSummaryService;
import com.api.readinglog.domain.member.entity.Member;
import com.api.readinglog.domain.member.service.MemberService;
import com.api.readinglog.domain.review.controller.dto.response.ReviewResponse;
import com.api.readinglog.domain.review.repository.ReviewRepository;
import com.api.readinglog.domain.summary.controller.dto.response.MySummaryResponse;
import com.api.readinglog.domain.summary.controller.dto.response.SummaryPageResponse;
import com.api.readinglog.domain.summary.controller.dto.response.SummaryResponse;
import com.api.readinglog.domain.summary.entity.Summary;
import com.api.readinglog.domain.summary.repository.SummaryRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BookLogService {

    private final MemberService memberService;
    private final BookService bookService;
    private final SummaryRepository summaryRepository;
    private final ReviewRepository reviewRepository;
    private final HighlightRepository highlightRepository;
    private final LikeSummaryService likeSummaryService;

    // 나의 로그 조회
    @Transactional(readOnly = true)
    public BookLogResponse myLogs(Long memberId, Long bookId) {
        Member member = getMember(memberId);
        Book book = getBook(bookId);
        BookDetailResponse bookDetailResponse = bookService.getBookInfo(memberId, bookId);

        // 한 줄평은 반드시 존재
        MySummaryResponse summary = findSummaryResponse(member, book);

        // 서평과 하이라이트가 존재하지 않은 경우는 빈 리스트 반환
        List<ReviewResponse> reviews = findReviewsResponse(member, book);
        List<HighlightResponse> highlights = findHighlightsResponse(member, book);

        return BookLogResponse.of(bookDetailResponse, summary, reviews, highlights);
    }

    // 북로그 목록 조회
    @Transactional(readOnly = true)
    public SummaryPageResponse bookLogs(Pageable pageable) {
        Slice<Summary> summaries = summaryRepository.findAllBy(pageable);

        // 북로그가 존재하지 않는 경우 예외 처리
        if (summaries.getContent().isEmpty()) {
            throw new SummaryException(ErrorCode.NOT_FOUND_BOOK_LOGS);
        }

        List<SummaryResponse> bookLogs = summaries.getContent().stream()
                .map(summary -> SummaryResponse.fromEntity(summary,
                        likeSummaryService.getSummaryLikeCount(summary.getId())))
                .collect(Collectors.toList());

        return SummaryPageResponse.fromSlice(bookLogs, summaries.hasNext());
    }

    // TODO: 북로그 상세 조회 (책, 회원, 한줄평, 서평, 하이라이트 모두)
    @Transactional(readOnly = true)
    public BookLogResponse bookLogDetails() {
        return null;
    }

    private Member getMember(Long memberId) {
        return memberService.getMemberById(memberId);
    }

    private Book getBook(Long bookId) {
        return bookService.getBookById(bookId);
    }

    private MySummaryResponse findSummaryResponse(Member member, Book book) {
        return summaryRepository.findByMemberAndBook(member, book)
                .map(MySummaryResponse::fromEntity)
                .orElseThrow(() -> new SummaryException(ErrorCode.NOT_FOUND_SUMMARY)); // 한 줄평이 존재하지 않는 경우 예외 처리
    }

    private List<ReviewResponse> findReviewsResponse(Member member, Book book) {
        return reviewRepository.findAllByMemberAndBook(member, book)
                .stream()
                .map(ReviewResponse::fromEntity)
                .collect(Collectors.toList());
    }

    private List<HighlightResponse> findHighlightsResponse(Member member, Book book) {
        return highlightRepository.findAllByMemberAndBook(member, book)
                .stream()
                .map(HighlightResponse::fromEntity)
                .collect(Collectors.toList());
    }
}

