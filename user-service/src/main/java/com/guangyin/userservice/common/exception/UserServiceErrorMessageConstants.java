package com.guangyin.userservice.common.exception;

/**
 * 错误信息常量类
 */
public class UserServiceErrorMessageConstants {
    /**
     * 用户注册失败
     */
    public static final String REGISTER_FAILED = "用户注册失败，请稍后再试";

    /**
     * 用户名已存在
     */
    public static final String USERNAME_ALREADY_EXISTS = "用户名已存在，请更换用户名后再试";

    /**
     * 绑定默认角色失败
     */
    public static final String BIND_DEFAULT_ROLE_FAILED = "绑定默认角色失败，请稍后再试";

    /**
     * 用户不存在
     */
    public static final String USER_NOT_EXISTS = "用户不存在";
    /**
     * 密码不匹配
     */
    public static final String PASSWORD_NOT_MATCH = "密码不匹配，请检查用户名和密码是否正确";
    /**
     * 没有权限修改密码
     */
    public static final String NO_PERMISSION_TO_CHANGE_PASSWORD = "没有权限修改密码，请联系管理员";
    /**
     * 旧密码不匹配
     */
    public static final String OLD_PASSWORD_NOT_MATCH = "旧密码不匹配，请检查后重试";
    /**
     * 修改密码失败
     */
    public static final String CHANGE_PASSWORD_FAILED = "修改密码失败，请稍后再试";
    /**
     * 退出登录失败
     */
    public static final String EXIT_FAILED = "退出登录失败，请稍后再试";
    /**
     * 更新用户信息失败
     */
    public static final String UPDATE_USER_FAILED = "更新用户信息失败，请稍后再试";
    /**
     * 只有超级管理员可以修改用户角色信息
     */
    public static final String ONLY_SUPER_ADMIN_CAN_CHANGE_ROLE = "只有超级管理员可以修改用户角色信息，请联系管理员进行操作";

    /**
     * 系统繁忙，请稍后再试
     */
    public static final String REMOTE_SERVICE_CALL_FAILED = "系统繁忙，请稍后再试";
}
