package com.guangyin.userservice.common.framework.core.utils;

import java.util.UUID;

/**
 * UUID工具类
 */
public class UUIDUtil {

    public static final String EMPTY_STR = "";
    public static final String HYPHEN_STR = "-";

    /**
     * 获取UUID字符串
     *
     * @return
     */
    public static String getUUID() {
        return UUID.randomUUID().toString().replace(HYPHEN_STR, EMPTY_STR).toUpperCase();
    }

}
