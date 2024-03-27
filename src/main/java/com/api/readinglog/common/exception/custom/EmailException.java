package com.api.readinglog.common.exception.custom;

import com.api.readinglog.common.exception.CustomException;
import com.api.readinglog.common.exception.ErrorCode;

public class EmailException extends CustomException {

    public EmailException(ErrorCode errorCode) {
        super(errorCode);
    }
}
