package com.api.readinglog.domain.review.repository;

import com.api.readinglog.domain.book.entity.Book;
import com.api.readinglog.domain.member.entity.Member;
import com.api.readinglog.domain.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findAllByMemberAndBook(Member member, Book book, Pageable pageable);
}
