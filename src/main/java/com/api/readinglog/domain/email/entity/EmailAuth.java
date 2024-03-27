package com.api.readinglog.domain.email.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String authCode;
    private LocalDateTime expiryTime;

    @Builder
    private EmailAuth(String email, String authCode, LocalDateTime expiryTime) {
        this.email = email;
        this.authCode = authCode;
        this.expiryTime = expiryTime;
    }

    public static EmailAuth of(String email, String authCode) {
        return EmailAuth.builder()
                .email(email)
                .authCode(authCode)
                .expiryTime(LocalDateTime.now().plusMinutes(5)) // 5분 후 만료
                .build();
    }
}
