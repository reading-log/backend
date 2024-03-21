package com.api.readinglog.common.oauth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuth2RevokeService {

    @Value("${social.revoke.googleUrl}")
    private String googleRevokeUrl;

    @Value("${social.revoke.naverUrl}")
    private String naverRevokeUrl;

    @Value("${social.revoke.kakaoUrl}")
    private String kakaoRevokeUrl;

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String naverClientId;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String naverClientSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    public void revokeKakao(String socialAccessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(socialAccessToken);
        sendRevokeRequest(kakaoRevokeUrl, new HttpEntity<>(headers));
    }

    public void revokeNaver(String socialAccessToken) {
        UriComponents uri = UriComponentsBuilder.fromUriString(naverRevokeUrl)
                .queryParam("grant_type", "delete")
                .queryParam("client_id", naverClientId)
                .queryParam("client_secret", naverClientSecret)
                .queryParam("access_token", socialAccessToken)
                .queryParam("service_provider", "naver")
                .build();

        sendRevokeRequest(uri.toString(), new HttpEntity<>(new HttpHeaders()));
    }

    public void revokeGoogle(String socialAccessToken) {
        UriComponents uri = UriComponentsBuilder.fromUriString(googleRevokeUrl)
                .queryParam("token", socialAccessToken)
                .build();

        sendRevokeRequest(uri.toString(), new HttpEntity<>(new HttpHeaders()));
    }

    private void sendRevokeRequest(String url, HttpEntity<Object> httpEntity) {
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        log.info("회원 탈퇴 상태코드: {}", responseEntity.getStatusCode());
        log.info("회원 탈퇴 결과: {}", responseEntity.getBody());
    }
}
