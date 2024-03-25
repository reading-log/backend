package com.api.readinglog.common.oauth;

import com.api.readinglog.common.jwt.JwtToken;
import com.api.readinglog.common.jwt.JwtTokenProvider;
import com.api.readinglog.common.security.util.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);

        // access token 헤더에 담기
        String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:5173/readinglog")
                .queryParam("accessToken", jwtToken.getAccessToken())
                .build().toUriString();

        // refresh token 쿠키에 담기
        String refreshToken = jwtToken.getRefreshToken();
        CookieUtils.addCookie(response, "refreshToken", refreshToken, 24 * 60 * 60 * 7); // 7일

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

}
