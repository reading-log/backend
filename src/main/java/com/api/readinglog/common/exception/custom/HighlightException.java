package com.api.readinglog.common.exception.custom;

import com.api.readinglog.common.exception.CustomException;
import com.api.readinglog.common.exception.ErrorCode;

public class HighlightException extends CustomException {
    public HighlightException(ErrorCode errorCode) {
        super(errorCode);
    }
}
