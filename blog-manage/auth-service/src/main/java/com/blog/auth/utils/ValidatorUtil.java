package com.blog.auth.utils;

import com.blog.auth.enums.LoginRelationConstants;
import com.blog.common.enums.ErrorCode;
import com.blog.common.exception.UserException;
import com.blog.common.utils.RedisUtil;

import java.util.regex.Pattern;

public class ValidatorUtil {
    private static final String REGEX_PASSWORD = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$";
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(REGEX_PASSWORD);

    public enum ValidationScenario {
        LOGIN, REGISTER
    }
    public static boolean isNullOrEmpty(Object obj) {
        return obj == null || "".equals(obj);
    }

    /**
     * 验证用户密码是否符合规范
     *
     * @param password 待验证的密码字符串，要求：
     *                 1. 长度在指定范围内（通过LoginRelationConstants常量获取最小最大值）
     *                 2. 符合预定义的正则表达式格式（包含数字、字母、特殊字符等组合）
     * @return boolean 验证通过返回true
     * @throws UserException 当出现以下情况时抛出：
     *                       1. 密码为空或null
     *                       2. 密码长度不符合要求
     *                       3. 密码格式不符合正则规则
     */
    public static boolean validatePassword(String password) {
        /* 基础空值检查 */
        if (isNullOrEmpty(password)) {
            throw new UserException(ErrorCode.ARGUMENT_IS_NULL);
        }

        /* 密码长度有效性验证 */
        int passwordLength = password.length();
        if (passwordLength < LoginRelationConstants.PASSWORD_LENGTH_MIN ||
                passwordLength > LoginRelationConstants.PASSWORD_LENGTH_MAX) {
            throw new UserException(ErrorCode.PASSWORD_LENGTH_ERROR);
        }

        /* 正则表达式格式验证 */
        // 检查密码是否符合格式
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new UserException(ErrorCode.PASSWORD_FORMAT_ERROR);
        }

        return true;
    }

    public static boolean validateEquals(Object str1, Object str2) {
        // 如果说有一个为空
        if (str1 == null || str2 == null) {
            // TODO 抛出校验的错误，其他的校验抛出的错误也需要修改一些， 暂时先这样放着
            throw new UserException(ErrorCode.ARGUMENT_IS_NULL);
        }

        // 如果都不空
        return str1.equals(str2);
    }


    /**
     * 验证用户名是否符合规范
     *
     * @param username 待验证的用户名字符串，不能为null或空字符串
     * @return boolean 验证通过返回true（验证失败时通过抛出异常中断流程）
     * @throws UserException 当用户名为空/空字符串时抛出ARGUMENT_IS_NULL错误码，
     *                       当用户名长度不符合要求时抛出USERNAME_LENGTH_ERROR错误码
     */
    public static boolean validateUsername(String username) {
        // 基础非空校验
        if (isNullOrEmpty(username)) {
            throw new UserException(ErrorCode.ARGUMENT_IS_NULL);
        }

        // 长度范围校验（使用预定义的常量值）
        int usernameLength = username.length();
        if (usernameLength < LoginRelationConstants.USERNAME_LENGTH_MIN ||
                usernameLength > LoginRelationConstants.USERNAME_LENGTH_MAX) {
            throw new UserException(ErrorCode.USERNAME_LENGTH_ERROR);
        }

        return true;
    }

    public static boolean validateCaptcha(String captcha, String captchaKey, RedisUtil redisUtil) {
        // 从缓存中得到验证码
        String storeCaptcha = (String) redisUtil.getStringCacheValue(captchaKey);
        if (storeCaptcha == null) {
            // 说明缓存中不存在验证码
            throw new UserException(ErrorCode.CAPTCHA_EXPIRE);
        }
        // 如果说存在
        if (!storeCaptcha.equalsIgnoreCase(captcha)) {
            // 说明验证码不匹配
            throw new UserException(ErrorCode.CAPTCHA_ERROR);
        }
        // 校验成功后删除缓存
        redisUtil.deleteCacheKeys(captchaKey);
        return true;
    }

}
