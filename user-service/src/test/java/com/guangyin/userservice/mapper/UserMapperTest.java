package com.guangyin.userservice.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.guangyin.userservice.entity.Users;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
@Slf4j
public class UserMapperTest {

    @Autowired
    private UsersMapper usersMapper;

    /**
     * 测试ShardingSphere的分库分表成功
     */
    @Test
    public void testInsertUserSuccess() {
        // 删除所有数据
        //usersMapper.delete(null);
        // 插入数据
        for(int i = 20; i < 40; ++i)
        {
            Users user = new Users();
            user.setPassword("123456");
            user.setEmail("lisi@example.com");
            user.setPhone("1000000000");
            user.setUsername("lisi" + i);
            int result = usersMapper.insert(user);
        }

        //查询全部数量
        Long count = usersMapper.selectCount(null);
        log.info("查询到的用户数量: {}", count);

        //查询username为lisi15的用户
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", "lisi20");
        Users user = usersMapper.selectOne(queryWrapper);
        Assert.assertNotNull(user);
        log.info("查询到的用户信息: {}", user);
    }
}
