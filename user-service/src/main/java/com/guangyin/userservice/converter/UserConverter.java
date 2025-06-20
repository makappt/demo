package com.guangyin.userservice.converter;

import com.guangyin.userservice.context.ChangePasswordContext;
import com.guangyin.userservice.context.UpdateUserContext;
import com.guangyin.userservice.context.UserLoginContext;
import com.guangyin.userservice.context.UserRegisterContext;
import com.guangyin.userservice.entity.Users;
import com.guangyin.userservice.po.ChangePasswordPO;
import com.guangyin.userservice.po.UpdateUserPO;
import com.guangyin.userservice.po.UserLoginPO;
import com.guangyin.userservice.po.UserRegisterPO;
import com.guangyin.userservice.vo.UserVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 用户模块实体转换工具类
 * <p>
 * NOTE mapstruct的使用
 * 转换原理：mapstruct会根据PO和Context的属性名称进行自动映射，生成一个临时的转换器类
 * 由于context和po的属性名称一致，所以可以直接使用mapstruct进行转换
 * 如果不一致，则需要使用@Mapping注解进行属性映射，如：
 *
 * @Mapping(source = "userName", target = "name")
 * userName是UserRegisterPO中的属性，name是UserRegisterContext中的属性
 */
@Mapper(componentModel = "spring")
public interface UserConverter {
    /**
     * 将用户注册PO转换为用户注册上下文
     *
     * @param userRegisterPO
     * @return
     */
    UserRegisterContext userRegisterPOToUserRegisterContext(UserRegisterPO userRegisterPO);

    /**
     * 将用户注册上下文转换为用户实体
     * 忽略密码字段，因为密码是明文，需要加密后存储
     *
     * @param context
     * @return
     */
    @Mapping(target = "password", ignore = true)
    Users userRegisterContextToUsers(UserRegisterContext context);

    /**
     * 将用户登录PO转换为用户登录上下文
     *
     * @param userLoginPO
     * @return
     */
    UserLoginContext userLoginPOToUserLoginContext(UserLoginPO userLoginPO);

    /**
     * 将用户实体转换为用户VO
     *
     * @param users
     * @return
     */
    UserVO usersToUserVO(Users users);

    /**
     * 将修改密码PO转换为修改密码上下文
     *
     * @param changePasswordPO
     * @return
     */
    ChangePasswordContext changePasswordPOToChangePasswordContext(ChangePasswordPO changePasswordPO);

    /**
     * 将用户更新PO转换为用户更新上下文
     *
     * @param updateUserPO
     * @return
     */
    UpdateUserContext updateUserPOToUpdateUserContext(UpdateUserPO updateUserPO) ;

    /**
     * 将用户更新上下文转换为用户实体
     *
     * @param context
     * @return
     */
    Users updateUserContextToUsers(UpdateUserContext context);
}
