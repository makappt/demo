package com.guangyin.userservice.controller;
import com.guangyin.core.response.Result;
import com.guangyin.userservice.po.ChangePasswordPo;
import com.guangyin.userservice.po.UserLoginPO;
import com.guangyin.userservice.po.UserRegisterPO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    /**
     * 用户注册
     *
     * @param userRegisterPO
     * @return
     */
    @PostMapping("/user/register")
    public Result register(@Validated @RequestBody UserRegisterPO userRegisterPO) {
        return Result.success();
    }

    /**
     * 用户登录
     *
     * @param userLoginPO 用户登录参数
     * @return 返回有时效性的jwt accessToken
     */
    @PostMapping("/user/login")
    public Result login(@Validated @RequestBody UserLoginPO userLoginPO) {
        return Result.success();
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
    public Result users()
    {
        return Result.success();
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
        return Result.success();
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
     *
     * 1.普通用户只能修改自己的密码
     * 2.管理员可以修改任意普通用户的密码
     * 3.超级管理员可以修改任意用户的密码
     *
     * @ChangePasswordPo 旧密码和新密码
     * @return
     */
    @PostMapping("/user/reset-password")
    public Result changePassword(@Validated @RequestBody ChangePasswordPo changePasswordPO) {
        return Result.success();
    }


}
