package com.api.readinglog.domain.like.service;

import com.api.readinglog.common.exception.ErrorCode;
import com.api.readinglog.common.exception.custom.SummaryException;
import com.api.readinglog.common.redis.service.RedisService;
import com.api.readinglog.domain.summary.entity.Summary;
import com.api.readinglog.domain.summary.repository.SummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeSummaryService {

    private static final String USER_LIKES_KEY = "user:%s:likes";
    private static final String SUMMARY_LIKES_KEY = "summary:%s:likes";

    private final RedisService redisService;
    private final SummaryRepository summaryRepository;

    // 좋아요 등록
    public void addLikeSummary(Long userId, Long summaryId) {
        validateSummaryExists(summaryId);

        if (!isLikeAlreadyExists(userId, summaryId)) {
            String userLikesKey = getUserLikesKey(userId);
            String summaryLikesKey = getSummaryLikesKey(summaryId);

            redisService.setLikeData(userLikesKey, summaryId);
            redisService.increaseLikeCount(summaryLikesKey);

            updateSummaryLikeCountInDb(summaryId, 1);
        }
    }

    // 좋아요 취소
    public void deleteLikeSummary(Long userId, Long summaryId) {
        validateSummaryExists(summaryId);

        if (isLikeAlreadyExists(userId, summaryId)) {
            String userLikesKey = getUserLikesKey(userId);
            String summaryLikesKey = getSummaryLikesKey(summaryId);

            redisService.deleteLikeData(userLikesKey, summaryId);
            redisService.decreaseLikeCount(summaryLikesKey);

            updateSummaryLikeCountInDb(summaryId, -1);
        }
    }

    // 좋아요 개수 조회
    public Integer getSummaryLikeCount(Long summaryId) {
        return redisService.getLikeCount(getSummaryLikesKey(summaryId));
    }

    // 유저의 좋아요 존재 여부 확인
    private boolean isLikeAlreadyExists(Long userId, Long summaryId) {
        String userLikesKey = getUserLikesKey(userId);
        return redisService.isPresent(userLikesKey, summaryId);
    }

    // DB 내 요약 좋아요 개수 업데이트
    private void updateSummaryLikeCountInDb(Long summaryId, int change) {
        Summary summary = summaryRepository.findById(summaryId)
                .orElseThrow(() -> new SummaryException(ErrorCode.NOT_FOUND_SUMMARY));
        summary.setLikeCount(summary.getLikeCount() + change);
    }

    // 한 줄평 존재 여부 확인
    private void validateSummaryExists(Long summaryId) {
        summaryRepository.findById(summaryId).orElseThrow(
                () -> new SummaryException(ErrorCode.NOT_FOUND_SUMMARY)
        );
    }

    private static String getUserLikesKey(Long userId) {
        return String.format(USER_LIKES_KEY, userId);
    }

    private static String getSummaryLikesKey(Long summaryId) {
        return String.format(SUMMARY_LIKES_KEY, summaryId);
    }
}
