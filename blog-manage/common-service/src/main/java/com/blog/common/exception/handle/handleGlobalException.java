package com.blog.exception.handle;

import com.blog.domain.vo.Result;
import com.blog.exception.BaseException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// 全局异常处理
@RestControllerAdvice
public class handleGlobalException {
    @ExceptionHandler(BaseException.class)
    public Result handleBaseException(BaseException e) {
        return Result.error(e.getMessage());
    }
}
