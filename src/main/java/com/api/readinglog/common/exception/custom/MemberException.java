package com.api.readinglog.common.exception.custom;

import com.api.readinglog.common.exception.CustomException;
import com.api.readinglog.common.exception.ErrorCode;

public class MemberException extends CustomException {
    public MemberException(ErrorCode errorCode) {
        super(errorCode);
    }
}
