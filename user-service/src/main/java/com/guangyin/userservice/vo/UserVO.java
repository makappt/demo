package com.guangyin.userservice.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserVO implements Serializable {

    private static final long serialVersionUID = 7782927503495923604L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 用户电话号码
     */
    private String phone;

    /**
     * 用户s身份
     */
    private String role;
}
