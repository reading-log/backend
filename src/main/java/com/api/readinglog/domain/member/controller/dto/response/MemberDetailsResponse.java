package com.api.readinglog.domain.member.controller.dto.response;

import com.api.readinglog.domain.member.entity.Member;
import com.api.readinglog.domain.member.entity.MemberRole;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberDetailsResponse {

    private Long id;
    private String email;
    private String nickname;
    private String password;
    private String profileImg;
    private MemberRole role;
    private LocalDateTime createdAt;

    @Builder
    private MemberDetailsResponse(Long id, String email, String nickname, String password, String profileImg,
                                 MemberRole role, LocalDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.profileImg = profileImg;
        this.role = role;
        this.createdAt = createdAt;
    }

    public static MemberDetailsResponse of(Member member, String profileImgUrl) {
        return MemberDetailsResponse.builder()
                .id(member.getId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .password(member.getPassword())
                .profileImg(profileImgUrl)
                .role(member.getRole())
                .createdAt(member.getCreatedAt())
                .build();
    }
}
