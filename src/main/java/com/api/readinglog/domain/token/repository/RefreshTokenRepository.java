package com.api.readinglog.domain.token.repository;

import com.api.readinglog.domain.token.entity.RefreshToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
    Optional<RefreshToken> findByMemberId(Long memberId);

    boolean existsByMemberId(Long id);
}
