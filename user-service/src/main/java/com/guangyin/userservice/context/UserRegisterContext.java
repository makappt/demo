package com.guangyin.userservice.context;

import com.guangyin.userservice.entity.Users;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserRegisterContext implements Serializable {
    /**
     * 序列化ID
     */
    private static final long serialVersionUID = 4396153266333816106L;

    /**
     * 用户名
     * 用户名只能包含数字和字母，长度为6-16位
     */
    private String username;

    /**
     * 密码
     * 密码长度为6-16位
     */
    private String password;

    /**
     * 邮箱
     * 邮箱格式: 用户名@域名.顶级域名
     */
    private String email;


    /**
     * 手机号码
     * 手机号码格式: 1开头的11位数字
     */
    private String phone;

    /**
     * 用户实体对象
     */
    Users entity;
}
