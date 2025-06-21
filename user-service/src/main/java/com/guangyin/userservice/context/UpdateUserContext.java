package com.guangyin.userservice.context;

import com.guangyin.userservice.entity.Users;
import lombok.Data;

import java.io.Serializable;

/**
 * 更新用户上下文
 * 用于封装更新用户信息的相关数据
 */
@Data
public class UpdateUserContext implements Serializable {
    private static final long serialVersionUID = 4299828493665850309L;
    /**
     * 被修改用户的用户ID
     */
    private Long userId;

    /**
     * 新用户名
     */
    private String username;

    /**
     * 新邮箱
     */
    private String email;

    /**
     * 新电话号码
     */
    private String phone;

    /**
     * 新身份
     */
    private Integer roleId;

    /**
     * 被修改的用户实体
     */
    Users entity;
}
