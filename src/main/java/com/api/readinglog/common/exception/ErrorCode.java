package com.api.readinglog.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 400
    EMPTY_SEARCH_KEYWORD("검색어를 입력해주세요!", HttpStatus.BAD_REQUEST),

    // 404
    NOT_FOUND_MEMBER("회원이 존재하지 않습니다!", HttpStatus.NOT_FOUND),
    NOT_FOUND_SEARCH("검색 결과가 존재하지 않습니다!", HttpStatus.NOT_FOUND),

    // 500
    INTERNAL_SERVER_ERROR("서버 에러 발생!", HttpStatus.INTERNAL_SERVER_ERROR),;

    private final String message;
    private final HttpStatus status;
}
