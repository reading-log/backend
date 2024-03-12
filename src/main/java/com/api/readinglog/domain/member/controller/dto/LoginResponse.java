package com.api.readinglog.domain.member.controller.dto;

import com.api.readinglog.common.jwt.JwtToken;
import lombok.Builder;
import lombok.Getter;

@Getter
public class LoginResponse {

    private final String grantType;
    private final String accessToken;
    private final String refreshToken;

    @Builder
    private LoginResponse(String accessToken, String refreshToken) {
        this.grantType = "Bearer";
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public static LoginResponse of(JwtToken jwtToken) {
        return LoginResponse.builder()
                .accessToken(jwtToken.getAccessToken())
                .refreshToken(jwtToken.getRefreshToken())
                .build();
    }

}
