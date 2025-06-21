package com.guangyin.userservice.po;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 用户登录PO（持久化对象）
 * 用于封装用户登录时的请求数据
 */
@Data
public class UserLoginPO implements Serializable {

    /**
     * 序列化ID
     */
    private static final long serialVersionUID = -3384406650970816608L;

    /**
     * 用户名
     * 用户名只能包含数字和字母，长度为6-16位
     */
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[0-9A-Za-z]{6,16}$", message = "用户名只能包含数字和字母，长度为6-16位")
    private String username;

    /**
     * 密码
     * 密码长度为6-16位
     */
    @NotBlank(message = "密码不能为空")
    @Length(min = 6, max = 16, message = "密码长度为6-16位")
    private String password;
}
