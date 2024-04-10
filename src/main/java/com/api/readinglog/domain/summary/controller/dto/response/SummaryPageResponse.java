package com.api.readinglog.domain.summary.controller.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SummaryPageResponse {
    private List<SummaryResponse> content;
    private boolean hasNext;

    @Builder
    private SummaryPageResponse(List<SummaryResponse> content, boolean hasNext) {
        this.content = content;
        this.hasNext = hasNext;
    }

    public static SummaryPageResponse fromSlice(List<SummaryResponse> content, boolean hasNext) {
        return SummaryPageResponse.builder()
                .content(content)
                .hasNext(hasNext)
                .build();
    }
}
