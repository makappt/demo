package com.guangyin.userservice.po;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Data
public class UpdateUserPO implements Serializable {

    private static final long serialVersionUID = 6717959399798156736L;

    /**
     * 用户名
     */
    private String username;

    /**
     * 邮箱
     * 邮箱格式: 用户名@域名.顶级域名
     */
    @Pattern(regexp = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$", message = "邮箱格式不正确")
    private String email;

    /**
     * 手机号码
     * 手机号码格式: 1开头的11位数字
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号码格式不正确")
    private String phone;

    /**
     * 身份ID 限定于1,2,3
     */
    @Min(value = 2, message = "无效的角色ID")
    @Max(value = 3, message = "无效的角色ID")
    private Integer roleId;
}
