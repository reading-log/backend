package com.api.readinglog.domain.summary.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
public class WriteRequest {

    @NotBlank(message = "본문 내용은 필수 입력값 입니다.")
    @Length(max = 100, message = "한줄평은 100자 이내로 작성해주세요.")
    private String content;

}



