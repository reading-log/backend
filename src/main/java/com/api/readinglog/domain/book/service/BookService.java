package com.api.readinglog.domain.book.service;

import com.api.readinglog.common.exception.ErrorCode;
import com.api.readinglog.common.exception.custom.BookException;
import com.api.readinglog.domain.book.dto.BookDirectRequest;
import com.api.readinglog.domain.book.dto.BookRegisterRequest;
import com.api.readinglog.domain.book.dto.BookSearchApiResponse;
import com.api.readinglog.domain.book.entity.Book;
import com.api.readinglog.domain.book.repository.BookRepository;
import com.api.readinglog.domain.member.entity.Member;
import com.api.readinglog.domain.member.service.MemberService;
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
    private final MemberService memberService;

    @Transactional(readOnly = true)
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

    // 책 자동 등록
    public void registerBookAfterSearch(Long memberId, BookRegisterRequest request) {
        Integer bookItemId = request.getItemId(); // 알라딘에서 제공해주는 책 고유 id
        // TODO: 내가 등록한 책 중에 해당 id로 등록된 책이 있으면 바로 상세 페이지로 이동
        /**
         * 이미 같은 책이 등록된 경우에는 나의 로그로 이동
         * 중복책 등록은 애초에 막아버리기
         */
        Member member = memberService.getMemberById(memberId);
        bookRepository.save(Book.of(member, request));
    }

    // 책 직접 등록
    public void registerBookDirect(Long memberId, BookDirectRequest request) {
        Member member = memberService.getMemberById(memberId);

        // TODO: 커버 이미지 s3 업로드
        bookRepository.save(Book.of(member, request));
    }
}
