package com.api.readinglog.domain.member.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberRole {

    MEMBER_NORMAL,
    MEMBER_GOOGLE,
    MEMBER_KAKAO,
    MEMBER_NAVER,
    ADMIN;

    public static MemberRole of(String provider) {
        return switch (provider.toLowerCase()) {
            case "google" -> MEMBER_GOOGLE;
            case "kakao" -> MEMBER_KAKAO;
            case "naver" -> MEMBER_NAVER;
            default -> MEMBER_NORMAL;
        };
    }
}
