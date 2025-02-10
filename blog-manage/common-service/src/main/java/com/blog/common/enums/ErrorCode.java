package com.blog.common.enums;

import io.swagger.annotations.ApiModel;

@ApiModel(value = "错误码枚举")
public enum ErrorCode {
    TOKEN_INVALID("0x0001", "Token无效"),
    TOKEN_EXPIRED("0x0002", "Token已过期"),
    TOKEN_FORMAT_ERROR("0x0003", "Token格式错误"),
    TOKEN_MISSING("0x0004", "Token缺失"),

    USER_NOT_LOGIN("0x0100", "用户未登录"),
    USER_NOT_EXIST("0x0101", "用户不存在"),
    USER_PASSWORD_ERROR("0x0102", "用户名或密码错误"),
    USER_LOCKED("0x0103", "用户已被锁定"),
    USER_DISABLED("0x0104", "用户已被禁用"),
    USER_EXPIRED("0x0105", "用户已过期"),
    USER_CREDENTIALS_EXPIRED("0x0106", "用户凭证已过期"),
    INVALID_USER_ID_FORMAT("0x107", "用户ID格式错误"),
    INVALID_USER_ID("0x108", "非法的用户ID"),


    ;
    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
     