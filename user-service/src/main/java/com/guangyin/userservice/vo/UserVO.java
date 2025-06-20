package com.guangyin.userservice.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserVO implements Serializable {

    private static final long serialVersionUID = 7782927503495923604L;

    private Long userId;

    private String username;

    private String email;

    private String phone;

    private String role;
}
