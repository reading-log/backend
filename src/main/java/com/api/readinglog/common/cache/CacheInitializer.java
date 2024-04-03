package com.api.readinglog.common.cache;

import com.api.readinglog.domain.like.service.LikeSummaryService;
import com.api.readinglog.domain.summary.entity.Summary;
import com.api.readinglog.domain.summary.repository.SummaryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CacheInitializer {

    private final LikeSummaryService likeSummaryService;
    private final SummaryRepository summaryRepository;

    // 서버 재시작 시 캐시의 좋아요 개수를 DB에 초기화
    @EventListener(ContextRefreshedEvent.class)
    public void initializeCacheWithSummaries(ContextRefreshedEvent event) {
        List<Summary> summaries = summaryRepository.findAll();

        summaries.forEach(summary -> {
            Long summaryId = summary.getId();
            int likeCount = likeSummaryService.getSummaryLikeCount(summaryId);

            summary.setLikeCount(likeCount);
            summaryRepository.save(summary);
        });
    }
}
