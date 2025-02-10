package com.blog.common.exception.handle;


import com.blog.common.domain.vo.Result;
import com.blog.common.exception.BaseException;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

// 全局异常处理
@RestControllerAdvice
public class HandleGlobalException {
    @ExceptionHandler(BaseException.class)
    public Result handleBaseException(BaseException e) {
        // 创建一个异常响应实体
        ExceptionResponseEntity exceptionResponseEntity = new ExceptionResponseEntity(
                e.getErrorCode(),
                Objects.toString(e.getMessage(), "系统内部错误，请稍后再试")
        );
        // 这个错误码是必须要设置
        exceptionResponseEntity.setErrorCode(e.getErrorCode());
        // 封装错误信息，返回给前端
        String message = String.format("错误码(%s) %s",
                exceptionResponseEntity.errorCode, exceptionResponseEntity.errorMessage);
        return Result.error(message);
    }

    @Data
    @AllArgsConstructor
    private static class ExceptionResponseEntity {
        private String errorCode;
        private String errorMessage;
    }
}
