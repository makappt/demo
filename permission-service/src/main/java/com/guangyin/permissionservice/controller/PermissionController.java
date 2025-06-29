package com.guangyin.permissionservice.controller;

import com.guangyin.permissionservice.common.annotation.OperationLog;
import com.guangyin.permissionservice.common.exception.PermissionServiceErrorMessageConstants;
import com.guangyin.permissionservice.common.framework.core.response.Result;
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
    @OperationLog(action = "bindDefaultRole", description = "绑定默认角色")
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
     * 绑定超级管理员
     *
     * @param userId
     * @return
     */
    @OperationLog(action = "bindSupperAdmin", description = "绑定超级管理员")
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
     * 查询用户身份码
     *
     * @param userId
     * @return
     */
    @OperationLog(action = "getUserRoleCode", description = "查询用户身份码")
    @GetMapping("/permission/role/get-code")
    public Result getUserRoleCode(Long userId) {
        if (Objects.isNull(userId) || userId <= 0) {
            return Result.fail(PermissionServiceErrorMessageConstants.PARAMETER_ERROR);
        }
        Integer roleCode = userRoleService.getUserRoleCode(userId);
        return Result.success(roleCode);
    }

    /**
     * 升级用户为管理员
     *
     * @param userId
     * @return
     */
    @OperationLog(action = "upgradeToAdmin", description = "升级用户为管理员")
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
    @OperationLog(action = "downgradeToUser", description = "降级用户为普通角色")
    @PostMapping("/permission/role/downgrade")
    public Result downgradeToUser(Long userId) {
        if (Objects.isNull(userId) || userId <= 0) {
            return Result.fail(PermissionServiceErrorMessageConstants.PARAMETER_ERROR);
        }
        Integer roleCode = userRoleService.downgradeToUser(userId);
        return Result.success(roleCode);
    }

}
