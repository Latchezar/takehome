package com.example.takehome.exceptions;

public enum ErrorCode {
    DATABASE_ERROR(10001, "Something went wrong, please send us feedback and try again"),
    USER_NOT_FOUND(12001, "User not found");

    private final Integer code;
    private final String description;

    ErrorCode(Integer code,
              String description) {
        this.code = code;
        this.description = description;
    }

    public static ErrorCode findByCode(Integer code) {
        if (code != null) {
            for (ErrorCode error : values()) {
                if (code.equals(error.code)) {
                    return error;
                }
            }
        }

        return null;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
