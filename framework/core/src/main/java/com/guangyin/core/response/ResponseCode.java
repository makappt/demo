package com.guangyin.core.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * AllArgsConstructor 用于生成一个包含所有字段的构造函数
 */
@AllArgsConstructor
@Getter
public enum ResponseCode {
    /**
     * 成功
     */
    SUCCESS(200, "SUCCESS"),
    /**
     * 错误
     */
    ERROR(1, "ERROR"),
    /**
     * token过期
     */
    TOKEN_EXPIRE(401, "TOKEN_EXPIRE"),
    /**
     * 参数错误
     */
    ERROR_PARAM(400, "ERROR_PARAM"),
    /**
     * 无权限访问
     */
    ACCESS_DENIED(403, "ACCESS_DENIED"),

    /**
     * 需要登录
     */
    NEED_LOGIN(401, "NEED_LOGIN");

    /**
     * 响应码
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String desc;
}
