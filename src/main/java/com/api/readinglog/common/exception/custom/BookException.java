package com.api.readinglog.common.exception.custom;

import com.api.readinglog.common.exception.CustomException;
import com.api.readinglog.common.exception.ErrorCode;

public class BookException extends CustomException {
    public BookException(ErrorCode errorCode) {
        super(errorCode);
    }
}
