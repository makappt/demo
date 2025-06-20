package com.guangyin.userservice.common.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {
    /**
     * 操作描述
     */
    String description();

    /**
     * 操作类型
     */
    String action();
}
