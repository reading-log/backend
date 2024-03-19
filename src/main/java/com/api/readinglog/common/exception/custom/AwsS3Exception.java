package com.api.readinglog.common.exception.custom;

import com.api.readinglog.common.exception.CustomException;
import com.api.readinglog.common.exception.ErrorCode;

public class AwsS3Exception extends CustomException {
    public AwsS3Exception(ErrorCode errorCode) {
        super(errorCode);
    }
}
