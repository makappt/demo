package com.guangyin.userservice.service;

import com.guangyin.userservice.context.UserRegisterContext;
import com.guangyin.userservice.entity.Users;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author zjz
* @description 针对表【users_0(用户信息表)】的数据库操作Service
* @createDate 2025-06-18 18:50:47
*/
public interface UserService extends IService<Users> {

    /**
     * 用户注册
     *
     * @param context 用户注册上下文
     * @return 注册成功的用户ID
     */
    Long register(UserRegisterContext context);
}
