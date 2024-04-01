package com.api.readinglog.domain.record.service;

import com.api.readinglog.domain.book.entity.Book;
import com.api.readinglog.domain.book.service.BookService;
import com.api.readinglog.domain.member.entity.Member;
import com.api.readinglog.domain.member.service.MemberService;
import com.api.readinglog.domain.record.controller.dto.RecordWriteRequest;
import com.api.readinglog.domain.record.entity.Record;
import com.api.readinglog.domain.record.repository.RecordRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RecordService {

    private final RecordRepository recordRepository;
    private final MemberService memberService;
    private final BookService bookService;

    public void write(long memberId, long bookId, RecordWriteRequest request) {
        Member member = memberService.getMemberById(memberId);
        Book book = bookService.getBookById(bookId);

        recordRepository.save(Record.of(member, book, request));
    }

}
