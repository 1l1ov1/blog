package com.blog.common.enums;

import io.swagger.annotations.ApiModel;

@ApiModel(value = "错误码枚举")
public enum ErrorCode {
    // Token 相关错误码
    TOKEN_INVALID("0x0001", "Token无效"),
    TOKEN_EXPIRED("0x0002", "Token已过期"),
    TOKEN_FORMAT_ERROR("0x0003", "Token格式错误"),
    TOKEN_MISSING("0x0004", "Token缺失"),

    // 用户相关错误码
    USER_NOT_LOGIN("0x0100", "用户未登录"),
    USER_NOT_EXIST("0x0101", "用户不存在"),
    USERNAME_OR_PASSWORD_ERROR("0x0102", "用户名或密码错误"),
    USER_LOCKED("0x0103", "用户已被锁定"),
    USER_DISABLED("0x0104", "用户已被禁用"),
    USER_EXPIRED("0x0105", "用户已过期"),
    USER_CREDENTIALS_EXPIRED("0x0106", "用户凭证已过期"),
    INVALID_USER_ID_FORMAT("0x0107", "用户ID格式错误"),
    INVALID_USER_ID("0x0108", "非法的用户ID"),
    USER_PASSWORD_ERROR("0x0109", "密码错误"),
    USER_USERNAME_ERROR("0x0110", "用户名错误"),
    USER_PHONE_ERROR("0x0111", "手机号错误"),
    CAPTCHA_ERROR("0x0112", "验证码错误"),
    CAPTCHA_EXPIRE("0x0113", "验证码已过期"),
    USERNAME_LENGTH_ERROR("0x0114", "用户名长度错误"),
    PASSWORD_LENGTH_ERROR("0x0115", "密码长度错误"),
    PASSWORD_FORMAT_ERROR("0x0116", "密码格式错误"),
    USER_EXIST("0x0117", "用户已存在"),
    ARGUMENT_IS_NULL("0x0200", "参数为空"),


;


    private final String code;
    private final String message;

    /**
     * 构造函数，用于初始化错误码和错误信息
     *
     * @param code    错误码
     * @param message 错误信息
     */
    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 获取错误码
     *
     * @return 错误码
     */
    public String getCode() {
        return code;
    }

    /**
     * 获取错误信息
     *
     * @return 错误信息
     */
    public String getMessage() {
        return message;
    }
}
