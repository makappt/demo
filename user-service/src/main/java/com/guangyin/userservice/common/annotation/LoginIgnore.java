package com.guangyin.userservice.common.annotation;

import java.lang.annotation.*;

/**
 * 登录忽略注解，在方法上使用该注释表示不需要进行登录校验
 * <p>
 * NOTE
 *  Retention 指定注释的保留测落位RUNTIME，表示注释在运行时可以通过反射机制访问。
 *  Target 指定注释可以应用于方法级别。
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LoginIgnore {

}
