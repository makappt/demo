package com.guangyin.userservice.client;

import com.guangyin.core.response.Result;
import com.guangyin.userservice.constants.UserConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * TODO 待处理
 */
@FeignClient(name = "permission-service")
public interface PermissionServiceClient {
    // 绑定默认角色（普通用户）
    @PostMapping("/permission/role/bind-default")
    Result bindDefaultRole(@RequestParam("userId") Long userId);

    @PostMapping("/permission/binSuperAdmin")
    Result bindSuperAdmin(@RequestParam("userId") Long userId);

    // 查询用户角色码（返回role_code）
    @GetMapping("/permission/role/get-code")
    Result<Integer> getUserRoleCode(@RequestParam("userId") Long userId);

    // 超管调用：升级用户为管理员
    @PostMapping("/permission/role/upgrade")
    Result upgradeToAdmin(@RequestParam("userId") Long userId);

    // 超管调用：降级用户为普通角色
    @PostMapping("/permission/role/downgrade")
    Result downgradeToUser(@RequestParam("userId") Long userId);


}
