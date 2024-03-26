package com.api.readinglog.domain.summary.repository;

import com.api.readinglog.domain.book.entity.Book;
import com.api.readinglog.domain.member.entity.Member;
import com.api.readinglog.domain.summary.entity.Summary;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SummaryRepository extends JpaRepository<Summary, Long> {

    Optional<Summary> findByMemberAndBook(Member member, Book book);
}
