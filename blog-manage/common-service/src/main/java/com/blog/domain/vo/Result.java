package com.blog.domain.vo;

import lombok.Data;

/**
 * 返回的结果
 */
@Data
/**
 * Result类用于封装操作结果，包括状态码、消息和数据
 * 它提供了一种统一的响应模型，适用于不同的操作场景
 */
public class Result {
    // 状态码
    private Integer code;
    // 消息
    private String msg;
    // 数据
    private Object data;

    /**
     * 私有构造函数，用于创建Result对象
     *
     * @param code 状态码
     * @param msg 消息
     * @param data 数据
     */
    private Result(Integer code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 公共构造函数，用于创建包含消息和数据的Result对象
     *
     * @param msg 消息
     * @param data 数据
     */
    public Result(String msg, Object data) {
        this.msg = msg;
        this.data = data;
    }

    /**
     * 公共构造函数，用于创建仅包含消息的Result对象
     *
     * @param msg 消息
     */
    public Result(String msg) {
        this.msg = msg;
    }

    /**
     * 创建一个成功的结果对象，包含指定的数据
     *
     * @param data 成功时返回的数据
     * @return 成功的结果对象
     */
    public static Result success(Object data) {
        return success(null, data);
    }

    /**
     * 创建一个成功的结果对象，包含指定的消息和数据
     *
     * @param msg 成功时的消息
     * @param data 成功时返回的数据
     * @return 成功的结果对象
     */
    public static Result success(String msg, Object data) {
        return new Result(ResultStatus.SUCCESS,msg, data);
    }

    /**
     * 创建一个错误的结果对象，包含指定的消息
     *
     * @param msg 错误时的消息
     * @return 错误的结果对象
     */
    public static Result error(String msg) {
        return error(msg, null);
    }

    /**
     * 创建一个错误的结果对象，包含指定的消息和数据
     *
     * @param msg 错误时的消息
     * @param data 错误时返回的数据
     * @return 错误的结果对象
     */
    public static Result error(String msg, Object data) {
        return new Result(ResultStatus.FAIL, msg, data);
    }

    /**
     * ResultStatus枚举类用于定义结果状态码
     */
    private static class ResultStatus {
        // 成功状态码
        public static final Integer SUCCESS = 1;
        // 失败状态码
        public static final Integer FAIL = -1;
    }
}
