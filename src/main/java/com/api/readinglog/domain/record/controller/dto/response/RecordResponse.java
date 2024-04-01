package com.api.readinglog.domain.record.controller.dto.response;

import com.api.readinglog.domain.record.entity.Record;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecordResponse {

    private Long memberId;
    private Long recordId;
    private Long bookId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public static RecordResponse fromEntity(Record record) {
        return RecordResponse.builder()
                .memberId((record.getMember().getId()))
                .recordId(record.getId())
                .bookId(record.getBook().getId())
                .startDate(record.getStartDate())
                .endDate(record.getEndDate())
                .build();
    }
}
