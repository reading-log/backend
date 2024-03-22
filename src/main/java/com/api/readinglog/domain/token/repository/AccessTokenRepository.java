package com.api.readinglog.domain.token.repository;

import com.api.readinglog.domain.token.entity.AccessToken;
import com.api.readinglog.domain.member.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessTokenRepository extends JpaRepository<AccessToken, Long> {
    Optional<AccessToken> findByMember(Member member);
}
