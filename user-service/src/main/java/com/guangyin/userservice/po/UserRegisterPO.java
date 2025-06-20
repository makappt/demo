package com.guangyin.userservice.po;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Data
public class UserRegisterPO implements Serializable {

    /**
     * 序列化ID
     */
    private static final long serialVersionUID = 994810901149937810L;

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

    /**
     * 邮箱
     * 邮箱格式: 用户名@域名.顶级域名
     */
    @NotBlank(message = "邮箱不能为空")
    @Pattern(regexp = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$", message = "邮箱格式不正确")
    private String email;


    /**
     * 手机号码
     * 手机号码格式: 1开头的11位数字
     */
    @NotBlank(message = "手机号码不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号码格式不正确")
    private String phone;
}
