package com.api.readinglog.domain.booklog.service;

import com.api.readinglog.common.exception.ErrorCode;
import com.api.readinglog.common.exception.custom.SummaryException;
import com.api.readinglog.domain.book.dto.BookDetailResponse;
import com.api.readinglog.domain.book.entity.Book;
import com.api.readinglog.domain.book.service.BookService;
import com.api.readinglog.domain.booklog.controller.dto.BookLogResponse;
import com.api.readinglog.domain.booklog.controller.dto.request.BookLogsSearchByBookTitleRequest;
import com.api.readinglog.domain.booklog.controller.dto.request.BookLogsSearchByCategoryRequest;
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

    // 북로그 상세 조회
    @Transactional(readOnly = true)
    public BookLogResponse bookLogDetails(Long memberId, Long bookId) {
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

    // 북로그 전체 목록 조회
    @Transactional(readOnly = true)
    public SummaryPageResponse bookLogs(Pageable pageable) {
        Slice<Summary> summaries = summaryRepository.findAllBy(pageable);

        List<SummaryResponse> bookLogs = summaries.getContent().stream()
                .map(summary -> SummaryResponse.fromEntity(summary,
                        likeSummaryService.getSummaryLikeCount(summary.getId())))
                .collect(Collectors.toList());

        return SummaryPageResponse.fromSlice(bookLogs, summaries.hasNext());
    }

    // 책 제목으로 검색
    public SummaryPageResponse findBookLogsByBookTitle(BookLogsSearchByBookTitleRequest request,
                                                       Pageable pageable) {
        List<Summary> summaries = summaryRepository.findByBookTitle(request.getBookTitle(), pageable);
        return createSummaryPageResponse(summaries, pageable);
    }

    // 카테고리명으로 검색
    public SummaryPageResponse findBookLogsByCategory(BookLogsSearchByCategoryRequest request,
                                                      Pageable pageable) {
        List<Summary> summaries = summaryRepository.findByCategoryName(request.getCategoryName(), pageable);
        return createSummaryPageResponse(summaries, pageable);
    }

    private SummaryPageResponse createSummaryPageResponse(List<Summary> summaries, Pageable pageable) {
        // 북로그가 존재하지 않는 경우 예외 처리
        if (summaries.isEmpty()) {
            throw new SummaryException(ErrorCode.NOT_FOUND_BOOK_LOGS);
        }

        List<SummaryResponse> content = buildSummaryResponse(summaries);

        // 마지막 페이지 여부 판별
        boolean hasNext = content.size() > pageable.getPageSize();
        if (hasNext) {
            content = content.subList(0, pageable.getPageSize());
        }
        return SummaryPageResponse.fromSlice(content, hasNext);
    }

    private List<SummaryResponse> buildSummaryResponse(List<Summary> summaries) {
        return summaries.stream()
                .map(summary -> SummaryResponse.fromEntity(summary,
                        likeSummaryService.getSummaryLikeCount(summary.getId())))
                .collect(Collectors.toList());
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

