package com.blog.auth.enums;

public enum InputType {
    USERNAME("username"),
    PHONE("phone"),
    ;
    private final String type;

    InputType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static InputType getLoginType(String type) {
        for (InputType inputType : InputType.values()) {
            if (inputType.getType().equals(type)) {
                return inputType;
            }
        }
        return null;
    }
}
