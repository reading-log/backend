package com.api.readinglog.domain.book.service;

import com.api.readinglog.common.aws.AmazonS3Service;
import com.api.readinglog.common.aws.DomainType;
import com.api.readinglog.common.exception.ErrorCode;
import com.api.readinglog.common.exception.custom.BookException;
import com.api.readinglog.common.exception.custom.MemberException;
import com.api.readinglog.common.image.ImageUtil;
import com.api.readinglog.domain.book.dto.BookDetailResponse;
import com.api.readinglog.domain.book.dto.BookDirectRequest;
import com.api.readinglog.domain.book.dto.BookModifyRequest;
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
    public BookDetailResponse getBookInfo(Long memberId, Long bookId) {
        Member member = memberService.getMemberById(memberId);

        Book book = bookRepository.findById(bookId).orElseThrow(() -> new BookException(ErrorCode.NOT_FOUND_BOOK));
        if (book.getMember() != member) {
            throw new MemberException(ErrorCode.NOT_MATCH_MEMBER);
        }

        String coverImgUrl = amazonS3Service.getFileUrl(book.getCover());
        return BookDetailResponse.fromEntity(book, coverImgUrl);
    }

    @Transactional(readOnly = true)
    public BookSearchApiResponse searchBooks(String query, int start) {

        if (query == null || query.isEmpty()) {
            throw new BookException(ErrorCode.EMPTY_SEARCH_KEYWORD);
        }

        BookSearchApiResponse response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/ItemSearch.aspx")
                        .queryParam("Query", query)
                        .queryParam("QueryType", "Title") // 제목으로 검색
                        .queryParam("SearchTarget", "Book") // 검색 대상: 도서
                        .queryParam("Start", start) // 시작 페이지: 1
                        .queryParam("MaxResults", "10") // 페이지 당 검색 결과: 10개
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
        String cover = amazonS3Service.uploadFile(request.getCover(), DomainType.BOOK);

        bookRepository.save(Book.of(member, request, cover));
    }

    public void modifyBook(Long memberId, Long bookId, BookModifyRequest bookModifyRequest) {
        Member member = memberService.getMemberById(memberId);
        Book book = getBookById(bookId);

        // 파일이 존재하면 기존 이미지 삭제 후 새로운 이미지 업로드
        String cover = book.getCover();

        if (!ImageUtil.isEmptyProfileImg(bookModifyRequest.getCover())) {
            amazonS3Service.deleteFile(cover);
            cover = amazonS3Service.uploadFile(bookModifyRequest.getCover(), DomainType.BOOK);
        }

        book.modify(bookModifyRequest, cover);
    }

    public void deleteBook(Long memberId, Long bookId) {
        Member member = memberService.getMemberById(memberId);
        Book book = getBookById(bookId);

        if (book.getMember() != member) {
            throw new BookException(ErrorCode.FORBIDDEN_DELETE);
        }

        bookRepository.delete(book);
    }

    public Book getBookById(Long bookId) {
        return bookRepository.findById(bookId).orElseThrow(() -> new BookException(ErrorCode.NOT_FOUND_BOOK));
    }
}
