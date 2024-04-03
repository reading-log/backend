package com.api.readinglog.domain.highlight.service;

import com.api.readinglog.common.exception.ErrorCode;
import com.api.readinglog.common.exception.custom.HighlightException;
import com.api.readinglog.domain.book.entity.Book;
import com.api.readinglog.domain.book.service.BookService;
import com.api.readinglog.domain.highlight.controller.dto.request.ModifyRequest;
import com.api.readinglog.domain.highlight.controller.dto.request.WriteRequest;
import com.api.readinglog.domain.highlight.controller.dto.response.HighlightResponse;
import com.api.readinglog.domain.highlight.entity.Highlight;
import com.api.readinglog.domain.highlight.repository.HighlightRepository;
import com.api.readinglog.domain.member.entity.Member;
import com.api.readinglog.domain.member.service.MemberService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class HighlightService {

    private final HighlightRepository highlightRepository;
    private final MemberService memberService;
    private final BookService bookService;

    @Transactional(readOnly = true)
    public List<HighlightResponse> highlights(Long memberId, Long bookId) {
        Member member = memberService.getMemberById(memberId);
        Book book = bookService.getBookById(bookId);

        List<HighlightResponse> highlights = highlightRepository.findAllByMemberAndBook(member, book)
                .stream()
                .map(HighlightResponse::fromEntity)
                .toList();

        // 하이라이트가 존재하지 않는 경우 예외 처리
        if(highlights.isEmpty()) {
            throw new HighlightException(ErrorCode.NOT_FOUND_HIGHLIGHT);
        }

        return highlights;
    }

    public void write(Long memberId, Long bookId, WriteRequest request) {
        Member member = memberService.getMemberById(memberId);
        Book book = bookService.getBookById(bookId);

        Highlight highlight = highlightRepository.save(Highlight.of(member, book, request));
        book.getHighlightList().add(highlight);
    }

    public void modify(Long memberId, Long highlightId, ModifyRequest request) {
        Member member = memberService.getMemberById(memberId);
        Highlight highlight = getHighlightById(highlightId);

        if (highlight.getMember() != member) {
            throw new HighlightException(ErrorCode.FORBIDDEN_MODIFY);
        }

        highlight.modify(request);
    }

    public void delete(Long memberId, Long highlightId) {
        Member member = memberService.getMemberById(memberId);
        Highlight highlight = getHighlightById(highlightId);

        if (highlight.getMember() != member) {
            throw new HighlightException(ErrorCode.FORBIDDEN_DELETE);
        }

        highlightRepository.delete(highlight);
    }

    public Highlight getHighlightById(Long highlightId) {
        return highlightRepository.findById(highlightId)
                .orElseThrow(() -> new HighlightException(ErrorCode.NOT_FOUND_HIGHLIGHT));
    }
}
