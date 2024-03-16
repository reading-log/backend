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

    // 404
    NOT_FOUND_MEMBER("존재하지 않은 회원입니다.", HttpStatus.NOT_FOUND),

    // 409
    MEMBER_ALREADY_EXISTS("이미 존재하는 회원입니다.", HttpStatus.CONFLICT),
    NICKNAME_ALREADY_EXISTS("이미 존재하는 닉네임입니다.", HttpStatus.CONFLICT),

    // 500
    INTERNAL_SERVER_ERROR("서버 에러 발생!", HttpStatus.INTERNAL_SERVER_ERROR),;

    private final String message;
    private final HttpStatus status;
}
