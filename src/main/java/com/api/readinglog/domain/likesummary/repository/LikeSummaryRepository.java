package com.api.readinglog.domain.likesummary.repository;

import com.api.readinglog.domain.likesummary.entity.LikeSummary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeSummaryRepository extends JpaRepository<LikeSummary, Long>, CustomLikeSummaryRepository {
    void deleteByMemberIdAndSummaryId(Long userId, Long summaryId);
}
