package com.guangyin.userservice.context;

import com.guangyin.userservice.entity.Users;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserLoginContext implements Serializable {

    private static final long serialVersionUID = 4553525972968277905L;

    private String username;

    private String password;

    private String accessToken;

    private Users entity;
}
