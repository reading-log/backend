package com.api.readinglog.common.exception.custom;

import com.api.readinglog.common.exception.CustomException;
import com.api.readinglog.common.exception.ErrorCode;

public class SummaryException extends CustomException {
    public SummaryException(ErrorCode errorCode) {
        super(errorCode);
    }
}
