package com.api.readinglog.domain.likesummary.repository;

public interface CustomLikeSummaryRepository {

    long getLikeTotalCountInMonth(Long memberId, int month);
}
