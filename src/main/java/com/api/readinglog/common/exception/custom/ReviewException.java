package com.api.readinglog.common.exception.custom;

import com.api.readinglog.common.exception.CustomException;
import com.api.readinglog.common.exception.ErrorCode;

public class ReviewException extends CustomException {
    public ReviewException(ErrorCode errorCode) {
        super(errorCode);
    }
}
