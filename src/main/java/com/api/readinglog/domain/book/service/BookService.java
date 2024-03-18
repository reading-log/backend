package com.api.readinglog.domain.book.service;

import com.api.readinglog.common.exception.ErrorCode;
import com.api.readinglog.common.exception.custom.BookException;
import com.api.readinglog.domain.book.dto.BookSearchApiResponse;
import com.api.readinglog.domain.book.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class BookService {

    private final WebClient webClient;
    private final BookRepository bookRepository;

    public BookSearchApiResponse searchBooks(String query, int start) {

        if (query == null || query.isEmpty()) {
            throw new BookException(ErrorCode.EMPTY_SEARCH_KEYWORD);
        }

        // TODO: 독서 기록이 있는 책인 경우 독서 기록도 응답에 포함시켜 전달.
        BookSearchApiResponse response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/ItemSearch.aspx")
                        .queryParam("Query", query) // 검색어
                        .queryParam("QueryType", "Keyword") // 제목 + 저자로 검색
                        .queryParam("SearchTarget", "Book") // 검색 대상: 도서
                        .queryParam("Start", start) // 시작 페이지: 1
                        .queryParam("MaxResults", "20") // 페이지 당 검색 결과: 20개
                        .queryParam("Sort", "Accuracy") // 관련도순 정렬
                        .build()
                )
                .retrieve()
                .bodyToMono(BookSearchApiResponse.class)
                .block();


        if (response == null || response.getTotalResults() == 0) {
            throw new BookException(ErrorCode.NOT_FOUND_SEARCH);
        }

        return response;
    }
}
