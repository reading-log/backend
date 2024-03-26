package com.api.readinglog.domain.review.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
public class ModifyRequest {

    @NotBlank(message = "본문 내용은 필수 입력값 입니다.")
    @Length(max = 1000, message = "서평은 1000자 이내로 작성해주세요.")
    private String content;
}
