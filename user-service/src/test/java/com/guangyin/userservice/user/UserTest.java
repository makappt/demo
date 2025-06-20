package com.guangyin.userservice.user;

import cn.hutool.core.lang.Assert;
import com.guangyin.core.exception.MicroServiceBusinessException;
import com.guangyin.userservice.context.UserLoginContext;
import com.guangyin.userservice.context.UserRegisterContext;
import com.guangyin.userservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Slf4j
@Transactional
public class UserTest {
    private static final String USERNAME = "testUser";
    private static final String PASSWORD = "testPassword";
    private static final String EMAIL = "testemail@qq.com";
    private static final String PHONE = "10000000000";

    @Autowired
    private UserService userService;

    @Test
    public void testUserRegisterSuccess()
    {
        UserRegisterContext context = createUserRegisterContext();
        Long registeredUserId = userService.register(context);
        Assert.isTrue(registeredUserId.longValue() > 0L);
    }


    @Test
    public void testUserLoginSuccess() {
        UserRegisterContext context = createUserRegisterContext();
        Long userId = userService.register(context);
        Assert.isTrue(userId.longValue() > 0L);

        UserLoginContext loginContext = createUserLoginContext();
        String accessToken = userService.login(loginContext);
        Assert.notBlank(accessToken, "Access token should not be blank");
    }

    /***********************************************private***********************************************/

    /**
     * 创建用户注册上下文
     *
     * @return
     */
    private static UserRegisterContext createUserRegisterContext() {
        UserRegisterContext context = new UserRegisterContext();
        context.setUsername(USERNAME);
        context.setPassword(PASSWORD);
        context.setEmail(EMAIL);
        context.setPhone(PHONE);
        return context;
    }

    /**
     * 创建用户登录上下文
     *
     * @return UserLoginContext
     */
    private static UserLoginContext createUserLoginContext() {
        UserLoginContext context = new UserLoginContext();
        context.setUsername(USERNAME);
        context.setPassword(PASSWORD);
        return context;
    }

}
