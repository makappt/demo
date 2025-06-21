package com.guangyin.userservice.context;

import com.guangyin.userservice.entity.Users;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录上下文
 * 用于存储用户登录相关信息
 */
@Data
public class UserLoginContext implements Serializable {

    private static final long serialVersionUID = 4553525972968277905L;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * jwt令牌
     */
    private String accessToken;

    /**
     * 用户实体
     */
    private Users entity;
}
