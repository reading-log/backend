package com.api.readinglog.common.exception.custom;

import com.api.readinglog.common.exception.CustomException;
import com.api.readinglog.common.exception.ErrorCode;

public class JwtException extends CustomException {
    public JwtException(ErrorCode errorCode) {
        super(errorCode);
    }
}
