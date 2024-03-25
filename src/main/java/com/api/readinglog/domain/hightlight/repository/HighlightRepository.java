package com.api.readinglog.domain.hightlight.repository;

import com.api.readinglog.domain.book.entity.Book;
import com.api.readinglog.domain.hightlight.entity.Highlight;
import com.api.readinglog.domain.member.entity.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HighlightRepository extends JpaRepository<Highlight, Long> {

    Slice<Highlight> findAllByMemberAndBook(Member member, Book book, Pageable pageable);
}
