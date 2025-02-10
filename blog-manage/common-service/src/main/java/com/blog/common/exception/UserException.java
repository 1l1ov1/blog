package com.blog.common.exception;

import com.blog.common.enums.ErrorCode;

public class UserException extends BaseException{

    public UserException(ErrorCode errorCode) {
        super(errorCode);
    }

    public UserException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public UserException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
