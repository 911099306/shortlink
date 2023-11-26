package com.offer.shortlink.admin.common.enums;

import com.offer.shortlink.admin.common.convention.errorcode.IErrorCode;

/**
 * @author serendipity
 * @version 1.0
 * @date 2023/11/26 20:42
 **/
public enum UserErrorCodeEnum implements IErrorCode {

    // ========== 用户记录不存在 ==========
    USER_NULL("B000200", "用户记录不存在"),
    // ========== 用户记录已存在 ==========
    USER_EXIST("B000201", "用户记录已存在");

    private final String code;

    private final String message;

    UserErrorCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
