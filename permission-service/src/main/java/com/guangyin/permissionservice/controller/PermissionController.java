package com.guangyin.permissionservice.controller;

import com.guangyin.core.response.Result;
import com.guangyin.permissionservice.common.exception.PermissionServiceErrorMessageConstants;
import com.guangyin.permissionservice.service.UserRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@Slf4j
public class PermissionController {

    @Autowired
    private UserRoleService userRoleService;

    /**
     * 绑定默认角色（普通用户）
     *
     * @param userId
     * @return
     */
    @PostMapping("/permission/role/bind-default")
    public Result bindDefaultRole(Long userId) {
        log.info("bindDefaultRole userId: {}", userId);
        if (Objects.isNull(userId) || userId <= 0) {
            return Result.fail(PermissionServiceErrorMessageConstants.PARAMETER_ERROR);
        }
        Long result = userRoleService.bindDefaultRole(userId);
        return Result.success(result);
    }

    /**
     * 超管调用：绑定超级管理员角色
     *
     * @param userId
     * @return
     */
    @PostMapping("/permission/binSuperAdmin")
    public Result bindSupperAdmin(Long userId) {
        log.info("bindSupperAdmin userId: {}", userId);
        if (Objects.isNull(userId) || userId <= 0) {
            return Result.fail(PermissionServiceErrorMessageConstants.PARAMETER_ERROR);
        }
        Long result = userRoleService.bindSupperAdmin(userId);
        return Result.success(result);
    }

    /**
     * 查询用户角色码（返回role_code）
     *
     * @param userId
     * @return
     */
    @GetMapping("/permission/role/get-code")
    public Result getUserRoleCode(Long userId) {
        if (Objects.isNull(userId) || userId <= 0) {
            return Result.fail(PermissionServiceErrorMessageConstants.PARAMETER_ERROR);
        }
        Integer roleCode = userRoleService.getUserRoleCode(userId);
        return Result.success(roleCode);
    }

    /**
     * 超管调用：升级用户为管理员
     *
     * @param userId
     * @return
     */
    @PostMapping("/permission/role/upgrade")
    public Result upgradeToAdmin(Long userId) {
        if (Objects.isNull(userId) || userId <= 0) {
            return Result.fail(PermissionServiceErrorMessageConstants.PARAMETER_ERROR);
        }
        Integer roleCode = userRoleService.upgradeToAdmin(userId);
        return Result.success(roleCode);

    }

    /**
     * 超管调用：降级用户为普通角色
     *
     * @param userId
     * @return
     */
    @PostMapping("/permission/role/downgrade")
    public Result downgradeToUser(Long userId) {
        if (Objects.isNull(userId) || userId <= 0) {
            return Result.fail(PermissionServiceErrorMessageConstants.PARAMETER_ERROR);
        }
        Integer roleCode = userRoleService.downgradeToUser(userId);
        return Result.success(roleCode);
    }

}
