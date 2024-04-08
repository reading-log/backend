package com.api.readinglog.domain.likesummary.entity;

import com.api.readinglog.common.base.BaseTimeEntity;
import com.api.readinglog.domain.member.entity.Member;
import com.api.readinglog.domain.summary.entity.Summary;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LikeSummary extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "summary_id", nullable = false)
    private Summary summary;

    @Builder
    private LikeSummary(Member member, Summary summary) {
        this.member = member;
        this.summary = summary;
    }

    public static LikeSummary of(Member member, Summary summary) {
        return LikeSummary.builder()
                .member(member)
                .summary(summary)
                .build();
    }
}
