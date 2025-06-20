package com.guangyin.userservice.service;

import com.guangyin.userservice.context.ChangePasswordContext;
import com.guangyin.userservice.context.UserLoginContext;
import com.guangyin.userservice.context.UserRegisterContext;
import com.guangyin.userservice.entity.Users;
import com.baomidou.mybatisplus.extension.service.IService;
import com.guangyin.userservice.vo.UserVO;

import java.util.List;

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

    /**
     * 用户登录
     *
     * @param context
     * @return
     */
    String login(UserLoginContext context);

    /**
     * 获取用户列表
     *
     * @return
     */
    List<UserVO> userList();

    /**
     * 修改密码
     *
     * @param context
     */
    void changePassword(ChangePasswordContext context);

    /**
     * 获取用户信息
     * 
     * @param userId
     * @return
     */
    UserVO info(Long userId);
}
