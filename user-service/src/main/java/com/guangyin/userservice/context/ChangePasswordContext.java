package com.guangyin.userservice.context;

import com.guangyin.userservice.entity.Users;
import lombok.Data;

import java.io.Serializable;

@Data
public class ChangePasswordContext implements Serializable {

    private static final long serialVersionUID = -1036122464987462122L;

    /**
     * 修改用户ID
     */
    private Long userId;

    /**
     * 旧密码
     */
    private String oldPassword;

    /**
     * 新密码
     */
    private String newPassword;

    /**
     * 用户实体对象
     */
    private Users entity;
}
