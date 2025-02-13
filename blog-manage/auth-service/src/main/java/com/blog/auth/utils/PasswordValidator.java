package com.blog.auth.utils;

public class PasswordValidator {

    // 检查字符串是否为空或空字符串
    private boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    // 检查密码是否包含至少一个大写字母、数字和特殊符号
    public boolean isValidPassword(String password) {
        // 如果密码为空或空字符串，直接返回 false
        if (isNullOrEmpty(password)) {
            return false;
        }

        // 检查密码是否包含至少一个大写字母
        boolean hasUpperCase = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUpperCase = true;
                break;
            }
        }

        // 检查密码是否包含至少一个数字
        boolean hasDigit = false;
        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) {
                hasDigit = true;
                break;
            }
        }

        // 检查密码是否包含至少一个特殊符号（例如：!@#$%^&*()）
        boolean hasSpecialChar = password.matches(".*[!@#$%^&*()].*");

        // 返回密码是否满足所有条件
        return hasUpperCase && hasDigit && hasSpecialChar;
    }

}