package com.api.readinglog.domain.token.repository;

import com.api.readinglog.domain.token.entity.SocialAccessToken;
import com.api.readinglog.domain.member.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SocialAccessTokenRepository extends JpaRepository<SocialAccessToken, Long> {
    Optional<SocialAccessToken> findByMember(Member member);
}
