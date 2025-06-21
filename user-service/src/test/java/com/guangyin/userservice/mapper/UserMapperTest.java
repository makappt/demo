package com.guangyin.userservice.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.guangyin.core.utils.PasswordUtil;
import com.guangyin.userservice.client.PermissionServiceClient;
import com.guangyin.userservice.entity.Users;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.N;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
@Slf4j
public class UserMapperTest {

    private static final String NAME = "testuser";
    private static final String EMAIL = "testemail@qq.com";
    private static final String PASSWORD = "testPassword";
    private static final String PHONE = "13800000000";
    private static final Integer ROLE_ID = 3;

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private PermissionServiceClient permissionServiceClient; // Assuming you have a client for this
    /**
     * 测试ShardingSphere的分库分表成功
     */
    @Test
    public void testInsertUserSuccess() {
        // 删除所有数据
        //usersMapper.delete(null);
        // 插入数据
        for(int i = 1; i < 20; ++i)
        {
            Users user = new Users();

            user.setEmail(EMAIL);
            user.setPhone(PHONE);
            user.setUsername(NAME + i);
            String salt = PasswordUtil.getSalt();
            user.setSalt(salt);
            user.setPassword(PasswordUtil.encryptPassword(salt, PASSWORD));
            user.setRoleId(ROLE_ID);
            user.setCreateTime(new java.util.Date());
            user.setUpdateTime(new java.util.Date());
            usersMapper.insert(user);
            permissionServiceClient.bindDefaultRole(user.getUserId());
        }
    }
}
