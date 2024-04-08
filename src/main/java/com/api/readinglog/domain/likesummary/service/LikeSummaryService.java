package com.api.readinglog.domain.likesummary.service;

import com.api.readinglog.common.exception.ErrorCode;
import com.api.readinglog.common.exception.custom.MemberException;
import com.api.readinglog.common.exception.custom.SummaryException;
import com.api.readinglog.common.redis.service.RedisService;
import com.api.readinglog.domain.likesummary.entity.LikeSummary;
import com.api.readinglog.domain.likesummary.repository.LikeSummaryRepository;
import com.api.readinglog.domain.member.entity.Member;
import com.api.readinglog.domain.member.repository.MemberRepository;
import com.api.readinglog.domain.summary.controller.dto.response.SummaryResponse;
import com.api.readinglog.domain.summary.entity.Summary;
import com.api.readinglog.domain.summary.repository.SummaryRepository;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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
    private final MemberRepository memberRepository;
    private final LikeSummaryRepository likeSummaryRepository;

    // 좋아요 등록
    public void addLikeSummary(Long userId, Long summaryId) {
        validateSummaryExists(summaryId);

        if (!isLikeAlreadyExists(userId, summaryId)) {
            String userLikesKey = getUserLikesKey(userId);
            String summaryLikesKey = getSummaryLikesKey(summaryId);

            redisService.setLikeData(userLikesKey, summaryId);
            redisService.increaseLikeCount(summaryLikesKey);

            updateSummaryLikeCountInDb(summaryId, 1);
            saveLikeSummaryInDb(userId, summaryId);
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
            removeLikeSummaryInDb(userId, summaryId);
        }
    }

    // 좋아요 개수 조회
    public Integer getSummaryLikeCount(Long summaryId) {
        return redisService.getLikeCount(getSummaryLikesKey(summaryId));
    }

    // 좋아요 등록 된 한줄평 목록 조회
    public List<SummaryResponse> getLikeSummaries(Long userId) {
        String userLikesKey = getUserLikesKey(userId);
        Set<Long> userLikes = redisService.getLikeData(userLikesKey);

        if (userLikes.isEmpty()) {
            return Collections.emptyList();
        }

        List<Summary> summaries = summaryRepository.findAllById(userLikes);

        return summaries.stream()
                .map(summary -> {
                    String summaryLikesKey = getSummaryLikesKey(summary.getId());
                    Integer likeCount = redisService.getLikeCount(summaryLikesKey);
                    return SummaryResponse.fromEntity(summary, likeCount);
                }).collect(Collectors.toList());
    }

    private void saveLikeSummaryInDb(Long userId, Long summaryId) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new MemberException(ErrorCode.NOT_FOUND_MEMBER));

        Summary summary = summaryRepository.findById(summaryId)
                .orElseThrow(() -> new SummaryException(ErrorCode.NOT_FOUND_SUMMARY));

        likeSummaryRepository.save(LikeSummary.of(member, summary));
    }

    private void removeLikeSummaryInDb(Long userId, Long summaryId) {
        likeSummaryRepository.deleteByMemberIdAndSummaryId(userId, summaryId);
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
