package com.guangyin.userservice.context;

import com.guangyin.userservice.entity.Users;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册上下文
 * 用于存储用户注册时的相关信息
 */
@Data
public class UserRegisterContext implements Serializable {
    /**
     * 序列化ID
     */
    private static final long serialVersionUID = 4396153266333816106L;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 邮箱
     */
    private String email;


    /**
     * 手机号码
     */
    private String phone;

    /**
     * 用户实体对象
     */
    Users entity;
}
