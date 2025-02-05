package com.blog.exception;

/**
 * token异常处理类
 */
public class TokenException extends BaseException {
    public TokenException(String message) {
        super(message);
    }

    public TokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
