package com.guangyin.permissionservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guangyin.permissionservice.common.enums.UserRoleEnum;
import com.guangyin.permissionservice.entity.UserRole;
import com.guangyin.permissionservice.service.UserRoleService;
import com.guangyin.permissionservice.mapper.UserRoleMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
* @author zjz
* @description 针对表【user_roles】的数据库操作Service实现
* @createDate 2025-06-19 13:57:43
*/
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole>
    implements UserRoleService{

    /**
     * 绑定默认角色给用户(普通用户)
     *
     * @param userId 用户ID
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long bindDefaultRole(Long userId) {

        QueryWrapper<UserRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("role_id", UserRoleEnum.USER.getCode());

        UserRole existingRole = this.getOne(queryWrapper);
        if (Objects.nonNull(existingRole)) {
            return existingRole.getId();
        }

        //模拟抛出异常
        //throw new RuntimeException("模拟异常");

        UserRole userRole = new UserRole();
        userRole.setRoleId(UserRoleEnum.USER.getCode());
        userRole.setUserId(userId);
        this.save(userRole);

        return userRole.getId();
    }

    @Override
    public Integer getUserRoleCode(Long userId) {
        QueryWrapper<UserRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        UserRole userRole = this.getOne(queryWrapper);
        if (Objects.isNull(userRole)) {
            return null;
        }
        return userRole.getRoleId();
    }
}




