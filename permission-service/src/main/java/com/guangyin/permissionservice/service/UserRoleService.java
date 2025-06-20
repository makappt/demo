package com.guangyin.permissionservice.service;

import com.guangyin.permissionservice.entity.UserRole;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author zjz
* @description 针对表【user_roles】的数据库操作Service
* @createDate 2025-06-19 13:57:43
*/
public interface UserRoleService extends IService<UserRole> {

    /**
     * 绑定用户默认角色
     *
     * @param userId 用户ID
     * @return
     */
    Long bindDefaultRole(Long userId);

    /**
     * 获取用户身份
     *
     * @param userId
     * @return
     */
    Integer getUserRoleCode(Long userId);
}
