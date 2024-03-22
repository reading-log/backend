package com.api.readinglog.common.webclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

@Configuration
public class WebClientConfig {

    @Value("${aladin.api-url}")
    private String ALADIN_API_URL;

    @Value("${aladin.ttbkey}")
    private String ttbKey;

    @Bean
    public WebClient webClient() {
        // 공통적으로 쓰이는 쿼리 파라미터를 포함시킨 baseUrl
        String baseUrlWithParam = UriComponentsBuilder.fromUriString(ALADIN_API_URL)
                .queryParam("TTBKey", ttbKey)
                .queryParam("Cover", "midbig")
                .queryParam("Output", "js")
                .queryParam("Version", "20131101")
                .build()
                .toUriString();

        return WebClient.builder()
                .baseUrl(baseUrlWithParam)
                .build();
    }
}
