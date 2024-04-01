package com.api.readinglog.domain.record.repository;

import com.api.readinglog.domain.book.entity.Book;
import com.api.readinglog.domain.member.entity.Member;
import com.api.readinglog.domain.record.entity.Record;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecordRepository extends JpaRepository<Record, Long> {

    List<Record> findAllByMemberAndBook(Member member, Book book);
}
