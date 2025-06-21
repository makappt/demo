package com.guangyin.permissionservice.common.enums;

import lombok.Getter;

/**
 * 用户角色枚举
 * <p>
 * 1. 超级管理员
 * 2. 管理员
 * 3. 普通用户
 */
@Getter
public enum UserRoleEnum {
    /**
     * 超级管理员
     */
    SUPER_ADMIN(1, "超级管理员"),

    /**
     * 管理员
     */
    ADMIN(2, "管理员"),

    /**
     * 普通用户
     */
    USER(3, "普通用户");

    private final Integer code;
    private final String desc;

    UserRoleEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
