package com.api.readinglog.domain.member.controller.dto;

import lombok.Getter;

@Getter
public class LoginRequest {

    private String email;
    private String password;
}
