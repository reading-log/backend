package com.api.readinglog.domain.member.entity;

import com.api.readinglog.common.base.BaseTimeEntity;
import com.api.readinglog.domain.member.controller.dto.JoinRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE member SET deleted_at = NOW() WHERE member_id = ?")
@Where(clause = "deleted_at IS NULL")
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "member_email")
    private String email;

    @Column(name = "member_nickname")
    private String nickname;

    @Column(name = "member_password")
    private String password;

    @Column(name = "member_profile_img")
    private String profileImg;

    @Column(name = "member_role")
    @Enumerated(EnumType.STRING)
    private MemberRole role;

    @Builder
    private Member(String email, String nickname, String password, String profileImg, MemberRole role) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.profileImg = profileImg;
        this.role = role;
    }

    public static Member of(String email, String nickname, String profileImg, MemberRole role) {
        return Member.builder()
                .email(email)
                .nickname(nickname)
                .profileImg(profileImg)
                .role(role)
                .build();
    }

    public static Member of(JoinRequest request, String password) {
        return Member.builder()
                .email(request.getEmail())
                .nickname(request.getNickname())
                .password(password)
                .profileImg(request.getProfileImage().getOriginalFilename())
                .role(MemberRole.MEMBER)
                .build();
    }

    public void updateProfile(String nickname, String picture) {
        this.nickname = nickname;
        this.profileImg = picture;
    }
}
