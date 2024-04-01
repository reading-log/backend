package com.api.readinglog.domain.record.service;

import com.api.readinglog.common.exception.ErrorCode;
import com.api.readinglog.common.exception.custom.RecordException;
import com.api.readinglog.domain.book.entity.Book;
import com.api.readinglog.domain.book.service.BookService;
import com.api.readinglog.domain.member.entity.Member;
import com.api.readinglog.domain.member.service.MemberService;
import com.api.readinglog.domain.record.controller.dto.request.RecordModifyRequest;
import com.api.readinglog.domain.record.controller.dto.request.RecordWriteRequest;
import com.api.readinglog.domain.record.controller.dto.response.RecordResponse;
import com.api.readinglog.domain.record.entity.Record;
import com.api.readinglog.domain.record.repository.RecordRepository;
import java.util.List;
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

    @Transactional(readOnly = true)
    public List<RecordResponse> getRecord(Long memberId, Long bookId) {
        Member member = memberService.getMemberById(memberId);
        Book book = bookService.getBookById(bookId);

        List<RecordResponse> records = recordRepository.findAllByMemberAndBook(member, book)
                .stream()

                .map(RecordResponse::fromEntity)
                .toList();

        if (records.isEmpty()) {
            throw new RecordException(ErrorCode.NOT_FOUND_RECORD);
        }

        return records;
    }

    public void write(long memberId, long bookId, RecordWriteRequest request) {
        Member member = memberService.getMemberById(memberId);
        Book book = bookService.getBookById(bookId);

        recordRepository.save(Record.of(member, book, request));
    }

    public void modify(Long memberId, Long recordId, RecordModifyRequest request) {
        Member member = memberService.getMemberById(memberId);
        Record record = getRecordById(recordId);

        if (record.getMember() != member) {
            throw new RecordException(ErrorCode.FORBIDDEN_MODIFY);
        }

        record.modify(request);
    }

    public void delete(Long memberId, Long recordId) {
        Member member = memberService.getMemberById(memberId);
        Record record = getRecordById(recordId);

        if (record.getMember() != member) {
            throw new RecordException(ErrorCode.FORBIDDEN_DELETE);
        }

        recordRepository.delete(record);
    }

    public Record getRecordById(Long recordId) {
        return recordRepository.findById(recordId).orElseThrow(() -> new RecordException(ErrorCode.NOT_FOUND_RECORD));
    }

}
