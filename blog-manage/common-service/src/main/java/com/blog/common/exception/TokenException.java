package com.blog.common.exception;

import com.blog.common.enums.ErrorCode;

/**
 * token异常处理类
 */
public class TokenException extends BaseException {
    public TokenException(ErrorCode errorCode) {
        super(errorCode);
    }

    public TokenException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public TokenException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
