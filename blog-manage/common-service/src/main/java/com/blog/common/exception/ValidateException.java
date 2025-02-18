package com.blog.common.exception;

import com.blog.common.enums.ErrorCode;

public class ValidateException extends BaseException{
    public ValidateException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ValidateException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public ValidateException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
