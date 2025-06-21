package com.guangyin.userservice.common.utils;

import com.guangyin.core.constants.MicroServiceConstants;

import java.util.Objects;

/**
 * 用户ID工具类
 * 在threadLocal中存储用户ID和获取用户ID
 */
public class UserIdUtil {

    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 设置当前线程的用户ID
     *
     * @param userId
     */
    public static void set(Long userId) {
        threadLocal.set(userId);
    }

    /**
     * 获取当前线程的用户ID
     *
     * @return
     */
    public static Long get() {
        Long userId = threadLocal.get();
        if (Objects.isNull(userId)) {
            return MicroServiceConstants.ZERO_LONG;
        }
        return userId;
    }

}
