package com.guangyin.userservice.runner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.guangyin.core.utils.IdUtil;
import com.guangyin.core.utils.PasswordUtil;
import com.guangyin.userservice.context.UserRegisterContext;
import com.guangyin.userservice.entity.Users;
import com.guangyin.userservice.service.UserService;
import com.guangyin.userservice.client.PermissionServiceClient; // Assuming you have a client for this
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

/**
 * 初始化超级管理员用户
 */
@Component
public class SuperAdminInitializer implements CommandLineRunner {

    private static final String SUPER_ADMIN_NAME = "superadmin";
    private static final String SUPER_ADMIN_EMAIL = "superadmin@example.com";
    private static final String SUPER_ADMIN_PASSWORD = "superadmin123";
    private static final String SUPER_ADMIN_PHONE = "13800000000";
    private static final Integer SUPER_ADMIN_ROLE_ID = 1;


    @Autowired
    private UserService userService;

    @Autowired
    private PermissionServiceClient permissionServiceClient; // Assuming you have a client for this

    @Override
    public void run(String... args) throws Exception {
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", SUPER_ADMIN_NAME);
        Users superadmin = userService.getOne(queryWrapper);

        if (Objects.isNull(superadmin)) {
            Users admin = new Users();
            admin.setUsername(SUPER_ADMIN_NAME);
            admin.setEmail(SUPER_ADMIN_EMAIL);
            admin.setPhone(SUPER_ADMIN_PHONE);
            admin.setRoleId(SUPER_ADMIN_ROLE_ID);
            admin.setCreateTime(new Date());
            admin.setUpdateTime(new Date());
            admin.setUserId(IdUtil.get());
            String salt = PasswordUtil.getSalt();
            admin.setSalt(salt);
            admin.setPassword(PasswordUtil.encryptPassword(salt, SUPER_ADMIN_PASSWORD));
            userService.save(admin);
            permissionServiceClient.bindSuperAdmin(admin.getUserId());
        }
    }
}