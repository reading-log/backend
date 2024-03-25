package com.api.readinglog.domain.book.service;

import com.api.readinglog.common.aws.AmazonS3Service;
import com.api.readinglog.common.exception.ErrorCode;
import com.api.readinglog.common.exception.custom.BookException;
import com.api.readinglog.common.exception.custom.MemberException;
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
    private final AmazonS3Service amazonS3Service;

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
        Member member = memberService.getMemberById(memberId);
        bookRepository.save(Book.of(member, request));
    }

    // 책 직접 등록
    public void registerBookDirect(Long memberId, BookDirectRequest request) {
        Member member = memberService.getMemberById(memberId);
        String cover = amazonS3Service.uploadBookCover(request.getCover());

        bookRepository.save(Book.of(member, request, cover));
    }

    public void deleteBook(Long memberId, Long bookId) {
        Member member = memberService.getMemberById(memberId);
        Book book = getBookById(bookId);

        if (book.getMember() != member) {
            throw new BookException(ErrorCode.FORBIDDEN_DELETE);
        }

        bookRepository.delete(book);
    }

    private Book getBookById(Long bookId) {
        return bookRepository.findById(bookId).orElseThrow(() -> new BookException(ErrorCode.NOT_FOUND_BOOK));
    }
}
