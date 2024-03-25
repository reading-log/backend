package com.api.readinglog.domain.hightlight.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
public class WriteRequest {

    @NotBlank(message = "본문 내용은 필수 입력값 입니다.")
    @Length(max = 500, message = "하이라이트는 500자 이내로 작성해주세요.")
    private String content;
}
