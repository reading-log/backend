package com.api.readinglog.domain.record.controller.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class RecordModifyRequest {

    @NotNull(message = "독서 시작일은 필수값 입니다.")
    private LocalDateTime startDate;

    @NotNull(message = "독서 종료일은 필수값 입니다.")
    private LocalDateTime endDate;
}
