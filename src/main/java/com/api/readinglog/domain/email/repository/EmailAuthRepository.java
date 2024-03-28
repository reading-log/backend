package com.api.readinglog.domain.email.repository;

import com.api.readinglog.domain.email.entity.EmailAuth;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailAuthRepository extends JpaRepository<EmailAuth, Long> {
    Optional<EmailAuth> findByEmailAndAuthCode(String email, String authCode);

    void deleteByExpiryTimeBefore(LocalDateTime now);
}
