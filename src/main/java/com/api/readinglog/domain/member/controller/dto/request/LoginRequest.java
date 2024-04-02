package com.api.readinglog.domain.member.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class LoginRequest {

    @Schema(description = "이메일", example = "test@test.com")
    private String email;

    @Schema(description = "비밀번호", example = "Password123!")
    private String password;
}
