package com.api.readinglog.domain.hightlight.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
public class ModifyRequest {

    @NotBlank(message = "본문 내용은 필수 입력값 입니다.")
    @Length(max = 500, message = "하이라이트는 500자 이내로 작성해주세요.")
    private String content;

    @NotNull(message = "페이지 번호는 필수 입력값 입니다.")
    private Integer page;
}
