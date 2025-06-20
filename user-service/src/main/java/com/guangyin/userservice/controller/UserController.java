package com.guangyin.userservice.controller;

import com.guangyin.core.response.Result;
import com.guangyin.userservice.common.annotation.LoginIgnore;
import com.guangyin.userservice.context.ChangePasswordContext;
import com.guangyin.userservice.context.UserLoginContext;
import com.guangyin.userservice.context.UserRegisterContext;
import com.guangyin.userservice.converter.UserConverter;
import com.guangyin.userservice.entity.Users;
import com.guangyin.userservice.po.ChangePasswordPO;
import com.guangyin.userservice.po.UserLoginPO;
import com.guangyin.userservice.po.UserRegisterPO;
import com.guangyin.userservice.service.UserService;
import com.guangyin.userservice.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserService usersService;

    @Autowired
    private UserConverter userConverter;

    /**
     * 用户注册
     * 无法重复注册
     *
     * @param userRegisterPO 用户名,密码,手机号,邮箱
     * @return 返回用户id
     */
    @LoginIgnore
    @PostMapping("/user/register")
    public Result register(@Validated @RequestBody UserRegisterPO userRegisterPO) {
        UserRegisterContext context = userConverter.userRegisterPOToUserRegisterContext(userRegisterPO);
        Long userId = usersService.register(context);
        return Result.success(userId);
    }

    /**
     * 用户登录
     *
     * @param userLoginPO 用户登录参数
     * @return 返回有时效性的jwt accessToken
     */
    @LoginIgnore
    @PostMapping("/user/login")
    public Result login(@Validated @RequestBody UserLoginPO userLoginPO) {
        UserLoginContext context = userConverter.userLoginPOToUserLoginContext(userLoginPO);
        String accessToken = usersService.login(context);
        return Result.success(accessToken);
    }

    /**
     * 分页用户列表
     * 1.普通用户仅返回自己的信息
     * 2.管理员可以返回所有普通用户的信息
     * 3.超级管理员可以返回所有用户的信息
     *
     * @return 用户信息列表
     */
    @GetMapping("users")
    public Result users() {
        List<UserVO> userList = usersService.userList();
        return Result.success(userList);
    }

    /**
     * 查询用户信息
     * 1.普通用户只能查询自己的信息
     * 2.管理员可以查询任意普通用户的信息
     * 3.超级管理员可以查询任意用户的信息
     *
     * @param userId 用户id
     * @return
     */
    @GetMapping("/user/{userId}")
    public Result info(@PathVariable Long userId) {
        UserVO userVO = usersService.info(userId);
        return Result.success(userVO);
    }

    /**
     * 更新用户信息
     * 1.普通用户只能更新自己的信息
     * 2.管理员可以更新任意普通用户的信息
     * 3.超级管理员可以更新任意用户的信息
     *
     * @param userId 用户id
     * @return
     */
    @PutMapping("/user/{userId}")
    public Result update(@PathVariable Long userId) {
        return Result.success();
    }

    /**
     * 修改密码
     * <p>
     * 1.普通用户只能修改自己的密码
     * 2.管理员可以修改任意普通用户的密码
     * 3.超级管理员可以修改任意用户的密码
     *
     * @return
     * @ChangePasswordPo 旧密码和新密码
     */
    @PostMapping("/user/reset-password")
    public Result changePassword(@Validated @RequestBody ChangePasswordPO changePasswordPO) {
        ChangePasswordContext context = userConverter.changePasswordPOToChangePasswordContext(changePasswordPO);
        usersService.changePassword(context);
        return Result.success();
    }


}
