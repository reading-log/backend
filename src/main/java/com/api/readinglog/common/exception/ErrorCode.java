package com.api.readinglog.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 400
    PASSWORD_MISMATCH("비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),

    // 401
    UNAUTHORIZED_LOGIN("로그인 실패: 인증에 실패하였습니다.", HttpStatus.UNAUTHORIZED),

    // 400
    EMPTY_SEARCH_KEYWORD("검색어를 입력해주세요!", HttpStatus.BAD_REQUEST),

    // 403
    FORBIDDEN_DELETE("삭제 권한이 없습니다!", HttpStatus.FORBIDDEN),
    FORBIDDEN_MODIFY("수정 권한이 없습니다!", HttpStatus.FORBIDDEN),
    NOT_MATCH_MEMBER("회원이 일치하지 않습니다!", HttpStatus.FORBIDDEN),


    // 404
    NOT_FOUND_MEMBER("회원이 존재하지 않습니다!", HttpStatus.NOT_FOUND),
    NOT_FOUND_SEARCH("검색 결과가 존재하지 않습니다!", HttpStatus.NOT_FOUND),
    NOT_FOUND_BOOK("등록된 책이 존재하지 않습니다!", HttpStatus.NOT_FOUND),
    NOT_FOUND_HIGHLIGHT("등록된 책이 존재하지 않습니다!", HttpStatus.NOT_FOUND),

    // 409
    MEMBER_ALREADY_EXISTS("이미 존재하는 회원입니다.", HttpStatus.CONFLICT),
    NICKNAME_ALREADY_EXISTS("이미 존재하는 닉네임입니다.", HttpStatus.CONFLICT),

    // 500
    INTERNAL_SERVER_ERROR("서버 에러 발생!", HttpStatus.INTERNAL_SERVER_ERROR),
    AWS_S3_FILE_UPLOAD_FAIL("AWS S3 파일 업로드 실패", HttpStatus.INTERNAL_SERVER_ERROR),
    AWS_S3_FILE_DELETE_FAIL("AWS S3 파일 삭제 실패", HttpStatus.INTERNAL_SERVER_ERROR),;

    private final String message;
    private final HttpStatus status;
}
