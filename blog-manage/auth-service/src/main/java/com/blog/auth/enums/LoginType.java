package com.blog.auth.enums;

public enum LoginType {
    USERNAME("username"),
    PHONE("phone"),
    ;
    private final String type;

    LoginType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static LoginType getLoginType(String type) {
        for (LoginType loginType : LoginType.values()) {
            if (loginType.getType().equals(type)) {
                return loginType;
            }
        }
        return null;
    }
}
