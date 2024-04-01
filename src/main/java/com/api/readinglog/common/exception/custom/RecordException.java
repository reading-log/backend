package com.api.readinglog.common.exception.custom;

import com.api.readinglog.common.exception.CustomException;
import com.api.readinglog.common.exception.ErrorCode;

public class RecordException extends CustomException {
    public RecordException(ErrorCode errorCode) {
        super(errorCode);
    }
}
