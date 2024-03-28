package com.api.readinglog.domain.email.dto;

import jakarta.validation.constraints.Email;
import lombok.Getter;

@Getter
public class AuthCodeVerificationRequest {

    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    private String authCode;
}
