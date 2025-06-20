package com.guangyin.userservice.po;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class ChangePasswordPO implements Serializable {

    /**
     * 序列化ID
     */
    private static final long serialVersionUID = -2790373805547240447L;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 旧密码
     */
    @NotBlank(message = "旧密码不能为空")
    @Length(min = 6, max = 16, message = "密码长度为6-16位")
    private String oldPassword;

    /**
     * 新密码
     */
    @NotBlank(message = "新密码不能为空")
    @Length(min = 6, max = 16, message = "密码长度为6-16位")
    private String newPassword;

}
