package com.guangyin.userservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guangyin.core.exception.MicroServiceBusinessException;
import com.guangyin.core.utils.IdUtil;
import com.guangyin.core.utils.PasswordUtil;
import com.guangyin.userservice.common.exception.UserServiceErrorMessageConstants;
import com.guangyin.userservice.context.UserRegisterContext;
import com.guangyin.userservice.converter.UserConverter;
import com.guangyin.userservice.entity.Users;
import com.guangyin.userservice.service.UserService;
import com.guangyin.userservice.mapper.UsersMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

/**
* @author zjz
* @description 针对表【users_0(用户信息表)】的数据库操作Service实现
* @createDate 2025-06-18 18:50:47
*/
@Service
public class UserServiceImpl extends ServiceImpl<UsersMapper, Users>
    implements UserService {

    @Autowired
    private UserConverter userConverter;

    /**
     * 用户注册
     * 1.幂等性：如果用户已存在，则不进行重复注册
     * 2.密码加密
     *
     * @param context 用户注册上下文，包含用户名、密码、邮箱、手机等信息
     * @return 用户ID
     */
    @Override
    public Long register(UserRegisterContext context) {
        assembleUserEntity(context);
        doRegister(context);
        return context.getEntity().getUserId();
    }

    /***********************************************private***********************************************/


    /**
     * 注册用户
     *
     * @param context 用户注册上下文
     */
    private void doRegister(UserRegisterContext context) {
        Users entity = context.getEntity();
        if (Objects.nonNull(entity)) {
            try {
                if (!save(entity)) {
                    throw new MicroServiceBusinessException(UserServiceErrorMessageConstants.REGISTER_FAILED);
                }
            } catch (DuplicateKeyException duplicateKeyException) {
                throw new MicroServiceBusinessException(UserServiceErrorMessageConstants.USERNAME_ALREADY_EXISTS);
            }
        }
    }

    /**
     * 组装用户实体
     *
     * @param context 用户注册上下文
     */
    private void assembleUserEntity(UserRegisterContext context) {
        Users entity = userConverter.userRegisterContextToUsers(context);
        String salt = PasswordUtil.getSalt();
        // 对密码进行加密
        String password = PasswordUtil.encryptPassword(salt, context.getPassword());
        entity.setUserId(IdUtil.get());
        entity.setSalt(salt);
        entity.setPassword(password);
        entity.setCreateTime(new Date());
        entity.setUpdateTime(new Date());
        context.setEntity(entity);
    }
}




