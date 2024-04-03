package com.api.readinglog.domain.email.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.Getter;

@Getter
public class EmailRequest {

    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @Schema(description = "사용자 이메일")
    private String email;
}
