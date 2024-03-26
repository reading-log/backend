package com.api.readinglog.common.security.util;

import com.api.readinglog.common.exception.ErrorCode;
import com.api.readinglog.common.exception.custom.MemberException;
import com.api.readinglog.common.jwt.JwtTokenProvider;
import com.api.readinglog.domain.token.repository.RefreshTokenRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtUtils {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    public void handleLogout(String refreshToken, HttpServletResponse response){
        if (refreshToken == null || !validateRefreshToken(refreshToken)) {
            throw new MemberException(ErrorCode.NOT_FOUND_REFRESH_TOKEN);
        }

        // 리프레시 토큰을 DB에서 삭제
        refreshTokenRepository.deleteByRefreshToken(refreshToken);
        deleteRefreshTokenCookie(response);

        response.setStatus(HttpServletResponse.SC_OK);
    }

    // 리프레시 토큰이 DB에 존재하고, 유효한 상태인지 검사
    private boolean validateRefreshToken(String refreshToken) {
        try {
            jwtTokenProvider.validateToken(refreshToken);
            return refreshTokenRepository.existsByRefreshToken(refreshToken);
        } catch (JwtException e) {
            return false;
        }
    }

    private void deleteRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", null);   // 쿠키의 리프레시 토큰을 삭제
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // HTTPS 환경에서만 적용되도록 설정
        response.addCookie(cookie);
    }
}

