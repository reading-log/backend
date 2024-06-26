package com.api.readinglog.domain.record.controller.dto.response;

import com.api.readinglog.domain.record.entity.Record;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecordResponse {

    private Long memberId;
    private Long bookId;
    private Long recordId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public static RecordResponse fromEntity(Record record) {
        return RecordResponse.builder()
                .memberId((record.getMember().getId()))
                .bookId(record.getBook().getId())
                .recordId(record.getId())
                .startDate(record.getStartDate())
                .endDate(record.getEndDate())
                .build();
    }
}
