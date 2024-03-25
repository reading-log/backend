package com.api.readinglog.domain.token.entity;

import com.api.readinglog.domain.member.entity.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SocialAccessToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String socialAccessToken;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    private SocialAccessToken(String socialAccessToken, Member member) {
        this.socialAccessToken = socialAccessToken;
        this.member = member;
    }

    public static SocialAccessToken of(String socialAccessToken, Member member) {
        return SocialAccessToken.builder()
                .socialAccessToken(socialAccessToken)
                .member(member)
                .build();
    }

    public void updateSocialAccessToken(String socialAccessToken) {
        this.socialAccessToken = socialAccessToken;
    }
}
