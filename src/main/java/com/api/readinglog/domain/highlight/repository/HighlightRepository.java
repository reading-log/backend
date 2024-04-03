package com.api.readinglog.domain.highlight.repository;

import com.api.readinglog.domain.book.entity.Book;
import com.api.readinglog.domain.highlight.entity.Highlight;
import com.api.readinglog.domain.member.entity.Member;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HighlightRepository extends JpaRepository<Highlight, Long> {

    List<Highlight> findAllByMemberAndBook(Member member, Book book);
}
