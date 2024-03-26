package com.api.readinglog.domain.summary.service;

import com.api.readinglog.common.exception.ErrorCode;
import com.api.readinglog.common.exception.custom.SummaryException;
import com.api.readinglog.domain.book.entity.Book;
import com.api.readinglog.domain.book.service.BookService;
import com.api.readinglog.domain.member.entity.Member;
import com.api.readinglog.domain.member.service.MemberService;
import com.api.readinglog.domain.summary.controller.dto.request.ModifyRequest;
import com.api.readinglog.domain.summary.controller.dto.request.WriteRequest;
import com.api.readinglog.domain.summary.controller.dto.response.SummaryResponse;
import com.api.readinglog.domain.summary.controller.dto.response.MySummaryResponse;
import com.api.readinglog.domain.summary.entity.Summary;
import com.api.readinglog.domain.summary.repository.SummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class SummaryService {

    private final SummaryRepository summaryRepository;
    private final MemberService memberService;
    private final BookService bookService;

    @Transactional(readOnly = true)
    public Page<SummaryResponse> feed(Pageable pageable) {
        Page<SummaryResponse> feed = summaryRepository.findAllBy(pageable).map(SummaryResponse::fromEntity);

        // 피드가 존재하지 않는 경우 예외 처리
        if (feed.getContent().isEmpty()) {
            throw new SummaryException(ErrorCode.NOT_FOUND_FEED);
        }

        return feed;
    }

    @Transactional(readOnly = true)
    public MySummaryResponse mySummary(Long memberId, Long bookId) {
        Member member = memberService.getMemberById(memberId);
        Book book = bookService.getBookById(bookId);

        // 해당 책에 대한 한줄평이 존재하면 반환
        Summary summary = summaryRepository.findByMemberAndBook(member, book)
                .orElseThrow(() -> new SummaryException(ErrorCode.NOT_FOUND_SUMMARY));

        return MySummaryResponse.fromEntity(summary);
    }

    public void write(Long memberId, Long bookId, WriteRequest request) {
        Member member = memberService.getMemberById(memberId);
        Book book = bookService.getBookById(bookId);

        // 해당 책에 대한 한줄평이 존재하는 경우 exception
        summaryRepository.findByMemberAndBook(member, book).ifPresent(it -> {
            throw new SummaryException(ErrorCode.SUMMARY_ALREADY_EXISTS);
        });

        Summary summary = Summary.of(member, book, request);
        summaryRepository.save(summary);
    }

    public void modify(Long memberId, Long summaryId, ModifyRequest request) {
        Member member = memberService.getMemberById(memberId);
        Summary summary = getSummaryById(summaryId);

        if (summary.getMember() != member) {
            throw new SummaryException(ErrorCode.FORBIDDEN_MODIFY);
        }

        summary.modify(request);
    }

    public void delete(Long memberId, Long summaryId) {
        Member member = memberService.getMemberById(memberId);
        Summary summary = getSummaryById(summaryId);

        if (summary.getMember() != member) {
            throw new SummaryException(ErrorCode.FORBIDDEN_DELETE);
        }

        summaryRepository.delete(summary);
    }

    public Summary getSummaryById(Long summaryId) {
        return summaryRepository.findById(summaryId)
                .orElseThrow(() -> new SummaryException(ErrorCode.NOT_FOUND_SUMMARY));
    }

}
