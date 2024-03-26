package com.api.readinglog.domain.review.repository;

import com.api.readinglog.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
