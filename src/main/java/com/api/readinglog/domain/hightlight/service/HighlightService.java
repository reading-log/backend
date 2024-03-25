package com.api.readinglog.domain.hightlight.service;

import com.api.readinglog.common.exception.ErrorCode;
import com.api.readinglog.common.exception.custom.HighlightException;
import com.api.readinglog.domain.book.entity.Book;
import com.api.readinglog.domain.book.service.BookService;
import com.api.readinglog.domain.hightlight.controller.dto.WriteRequest;
import com.api.readinglog.domain.hightlight.entity.Highlight;
import com.api.readinglog.domain.hightlight.repository.HighlightRepository;
import com.api.readinglog.domain.member.entity.Member;
import com.api.readinglog.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.integration.IntegrationProperties.Error;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class HighlightService {

    private final HighlightRepository highlightRepository;
    private final MemberService memberService;
    private final BookService bookService;

    public void write(Long memberId, Long bookId, WriteRequest request) {
        Member member = memberService.getMemberById(memberId);
        Book book = bookService.getBookById(bookId);

        Highlight highlight = Highlight.of(member, book, request.getContent());
        highlightRepository.save(highlight);
    }

    public void delete(Long memberId, Long highlightId) {
        Member member = memberService.getMemberById(memberId);
        Highlight highlight = getHighlightById(highlightId);

        if (highlight.getMember() != member) {
            throw new HighlightException(ErrorCode.NOT_MATCH_MEMBER);
        }

        highlightRepository.delete(highlight);
    }

    public Highlight getHighlightById(Long highlightId) {
        return highlightRepository.findById(highlightId)
                .orElseThrow(() -> new HighlightException(ErrorCode.NOT_FOUND_HIGHLIGHT));
    }

}
