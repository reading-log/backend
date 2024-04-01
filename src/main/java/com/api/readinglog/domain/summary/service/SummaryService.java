package com.api.readinglog.domain.summary.service;

import com.api.readinglog.common.exception.ErrorCode;
import com.api.readinglog.common.exception.custom.SummaryException;
import com.api.readinglog.domain.book.entity.Book;
import com.api.readinglog.domain.book.service.BookService;
import com.api.readinglog.domain.member.entity.Member;
import com.api.readinglog.domain.member.service.MemberService;
import com.api.readinglog.domain.summary.controller.dto.request.ModifyRequest;
import com.api.readinglog.domain.summary.controller.dto.request.WriteRequest;
import com.api.readinglog.domain.summary.entity.Summary;
import com.api.readinglog.domain.summary.repository.SummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class SummaryService {

    private final SummaryRepository summaryRepository;
    private final MemberService memberService;
    private final BookService bookService;

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
