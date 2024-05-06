package com.api.readinglog.domain.review.repository;

import com.api.readinglog.domain.book.entity.Book;
import com.api.readinglog.domain.member.entity.Member;
import com.api.readinglog.domain.review.entity.Review;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<Review> findByMemberAndBook(Member member, Book book);
    List<Review> findAllByMemberAndBook(Member member, Book book);
}
