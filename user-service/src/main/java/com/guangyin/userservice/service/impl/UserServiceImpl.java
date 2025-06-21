package com.guangyin.userservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guangyin.cachecore.constants.CacheConstants;
import com.guangyin.core.exception.MicroServiceBusinessException;
import com.guangyin.core.response.Result;
import com.guangyin.core.utils.IdUtil;
import com.guangyin.core.utils.JwtUtil;
import com.guangyin.core.utils.PasswordUtil;
import com.guangyin.userservice.client.PermissionServiceClient;
import com.guangyin.userservice.common.enums.UserRoleEnum;
import com.guangyin.userservice.common.exception.UserServiceErrorMessageConstants;
import com.guangyin.userservice.common.utils.UserIdUtil;
import com.guangyin.userservice.constants.UserConstants;
import com.guangyin.userservice.context.ChangePasswordContext;
import com.guangyin.userservice.context.UpdateUserContext;
import com.guangyin.userservice.context.UserLoginContext;
import com.guangyin.userservice.context.UserRegisterContext;
import com.guangyin.userservice.converter.UserConverter;
import com.guangyin.userservice.entity.Users;
import com.guangyin.userservice.service.UserService;
import com.guangyin.userservice.mapper.UsersMapper;
import com.guangyin.userservice.vo.UserVO;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zjz
 * @description 针对表【users_0(用户信息表)】的数据库操作Service实现
 * @createDate 2025-06-18 18:50:47
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UsersMapper, Users>
        implements UserService {

    @Autowired
    private UserConverter userConverter;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private PermissionServiceClient permissionServiceClient;

    /**
     * 用户注册
     * 1.幂等性：如果用户已存在，则不进行重复注册
     * 2.密码加密
     * 3.通过rpc调用绑定用户权限
     *
     * @param context 用户注册上下文，包含用户名、密码、邮箱、手机等信息
     * @return 用户ID
     */
    @Override
    @GlobalTransactional(name = "user-register-tx", rollbackFor = Exception.class)
    public Long register(UserRegisterContext context) {
        Users dbUser = getUsersByUsername(context.getUsername());
        if (Objects.nonNull(dbUser)) {
            throw new MicroServiceBusinessException(UserServiceErrorMessageConstants.USERNAME_ALREADY_EXISTS);
        }

        assembleUserEntity(context);
        doRegister(context);
        return context.getEntity().getUserId();
    }

    /**
     * 用户登录
     * 1.验证用户名和密码
     * 2.生成具有时效性的accessToken
     * 3.将accessToken存储在缓存中,实现单机登录
     *
     * @param context
     * @return
     */
    @Override
    public String login(UserLoginContext context) {
        // 校验用户登录信息
        checkLoginInfo(context);
        // 生成并保存用户的accessToken
        generateAndSaveAccessToken(context);

        return context.getAccessToken();
    }

    /**
     * 获取用户列表
     * 1.普通用户仅返回自己的信息
     * 2.管理员可以返回所有普通用户的信息
     * 3.超级管理员可以返回所有用户的信息
     *
     * @return
     */
    public Page<UserVO> userList(Page<Users> pageRequest) {
        Long userId = UserIdUtil.get();
        //获取用户权限
        Result<Integer> userRoleCode = permissionServiceClient.getUserRoleCode(userId);
        Integer roleCode = userRoleCode.getData();

        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();

        if (Objects.equals(roleCode, UserRoleEnum.USER.getCode())) {
            queryWrapper.eq("user_id", userId);
        }
        else if (Objects.equals(roleCode, UserRoleEnum.ADMIN.getCode())) {
            queryWrapper.eq("role_id", UserRoleEnum.USER.getCode());
        }
        else {
            queryWrapper.ne("role_id", UserRoleEnum.SUPER_ADMIN.getCode());
        }
        Page<Users> usersPage = this.page(pageRequest, queryWrapper);
        Page<UserVO> userVOPage = new Page<>(usersPage.getCurrent(), usersPage.getSize(), usersPage.getTotal());
        userVOPage.setRecords(convertUsersListToVO(usersPage.getRecords()));
        return userVOPage;
    }

    private List<UserVO> convertUsersListToVO(List<Users> userList) {
        if (userList == null || userList.isEmpty()) {
            return Collections.emptyList();
        }
        return userList.stream().map(user -> {
            UserVO userVO = userConverter.usersToUserVO(user);
            userVO.setRole(getRoleName(user.getRoleId())); // Set the role description
            return userVO;
        }).collect(Collectors.toList());
    }

    @Override
    public void changePassword(ChangePasswordContext context) {
        Long userId = UserIdUtil.get();
        Long changeUserId = context.getUserId();
        //获取要修改用户的信息
        Users user = getById(changeUserId);
        context.setEntity(user);
        //如果是自己修改自己的密码
        if (Objects.equals(changeUserId, userId)) {
            //校验旧密码
            checkOldPassword(context);
        }
        //否则就是管理员修改用户密码或超级管理员修改任意用户密码
        else {
            checkUserPermission(userId, changeUserId);
        }
        //修改用户密码
        doChangePassword(context);
        //退出当前登录状态
        exitLoginStatus(context);
    }

    @Override
    public UserVO info(Long userId) {
        //如果是自己查询自己的信息
        Long currentId = UserIdUtil.get();
        if (Objects.equals(userId, currentId)) {
            Users user = getById(userId);
            UserVO userVO = userConverter.usersToUserVO(user);
            userVO.setRole(getRoleName(user.getRoleId()));
            return userVO;
        }
        //否则是管理员或超级管理员查询其他用户信息
        else
        {
            //确认权限是否符合
            checkUserPermission(currentId, userId);
            Users user = getById(userId);
            if (Objects.isNull(user)) {
                throw new MicroServiceBusinessException(UserServiceErrorMessageConstants.USER_NOT_EXISTS);
            }
            UserVO userVO = userConverter.usersToUserVO(user);
            userVO.setRole(getRoleName(user.getRoleId()));
            return userVO;
        }
    }

    @Override
    public void update(UpdateUserContext context) {
        // 如果是自己更新自己的信息
        Long currentUserId = UserIdUtil.get();
        Long userId = context.getUserId();
        if(Objects.equals(currentUserId, userId)) {
            if(Objects.nonNull(context.getRoleId())) {
                throw new MicroServiceBusinessException(UserServiceErrorMessageConstants.ONLY_SUPER_ADMIN_CAN_CHANGE_ROLE);
            }
            // 更新自己的信息
            Users user = userConverter.updateUserContextToUsers(context);
            user.setUpdateTime(new Date());
            if (!updateById(user)) {
                throw new MicroServiceBusinessException(UserServiceErrorMessageConstants.UPDATE_USER_FAILED);
            }
        }
        else
        {
            // 如果是管理员或超级管理员更新其他用户信息
            Integer currentUserRoleCode = permissionServiceClient.getUserRoleCode(currentUserId).getData();
            Integer UserRoleCode = permissionServiceClient.getUserRoleCode(userId).getData();
            //如果修改人的权限比被修改人的权限低，则抛出异常
            if (currentUserRoleCode >= UserRoleCode) {
                throw new MicroServiceBusinessException(UserServiceErrorMessageConstants.NO_PERMISSION_TO_CHANGE_PASSWORD);
            }
            // 如果不是超级管理员且要修改角色，则抛出异常
            if(!Objects.equals(currentUserRoleCode,UserRoleEnum.SUPER_ADMIN.getCode()) && Objects.nonNull(context.getRoleId())) {
                throw new MicroServiceBusinessException(UserServiceErrorMessageConstants.ONLY_SUPER_ADMIN_CAN_CHANGE_ROLE);
            }
            if(Objects.equals(currentUserRoleCode,UserRoleEnum.SUPER_ADMIN.getCode()) && Objects.nonNull(context.getRoleId())) {
                //判断降级还是升级
                if( Objects.equals(context.getRoleId(), UserRoleEnum.ADMIN.getCode())) {
                    permissionServiceClient.upgradeToAdmin(context.getUserId());
                }
                else if(Objects.equals(context.getRoleId(), UserRoleEnum.USER.getCode())) {
                    permissionServiceClient.downgradeToUser(context.getUserId());
                }
            }
            Users user = userConverter.updateUserContextToUsers(context);
            user.setUpdateTime(new Date());
            if (!updateById(user)) {
                throw new MicroServiceBusinessException(UserServiceErrorMessageConstants.UPDATE_USER_FAILED);
            }
        }
    }


    /***********************************************private***********************************************/
    /**
     * 获取权限名称
     *
     * @param roleId
     * @return
     */
    private String getRoleName(Integer roleId) {
        if (Objects.equals(roleId, UserRoleEnum.ADMIN.getCode())) {
            return UserRoleEnum.ADMIN.getDesc();
        } else if (Objects.equals(roleId, UserRoleEnum.SUPER_ADMIN.getCode())) {
            return UserRoleEnum.SUPER_ADMIN.getDesc();
        } else {
            return UserRoleEnum.USER.getDesc();
        }
    }

    /**
     * 校验用户权限
     * 1.同级权限无法修改
     * 2.管理员可以修改普通用户密码
     * 3.超级管理员可以修改任意用户密码
     *
     * @param currentUserId
     * @param UserId
     */
    private void checkUserPermission(Long currentUserId, Long UserId) {
        Integer currentUserRoleCode = permissionServiceClient.getUserRoleCode(currentUserId).getData();
        Integer UserRoleCode = permissionServiceClient.getUserRoleCode(UserId).getData();
        //如果修改人的权限比被修改人的权限低，则抛出异常
        if (currentUserRoleCode >= UserRoleCode) {
            throw new MicroServiceBusinessException(UserServiceErrorMessageConstants.NO_PERMISSION_TO_CHANGE_PASSWORD);
        }
    }

    /**
     * 退出当前登录状态
     *
     * @param context
     */
    private void exitLoginStatus(ChangePasswordContext context) {
        exit(context.getUserId());
    }

    /**
     * 用户退出登录
     * 清除缓存
     *
     * @param userId
     */
    private void exit(Long userId) {
        try {
            // 获取redis的缓存
            Cache cache = cacheManager.getCache(CacheConstants.MICRO_SERVICE_CACHE_NAME);
            // 立即清除用户登录缓存
            cache.evictIfPresent(UserConstants.USER_LOGIN_PREFIX + userId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new MicroServiceBusinessException(UserServiceErrorMessageConstants.EXIT_FAILED);
        }
    }

    /**
     * 修改密码
     *
     * @param context
     */
    private void doChangePassword(ChangePasswordContext context) {
        String newPassword = context.getNewPassword();
        Users user = context.getEntity();
        String salt = user.getSalt();
        String newDbPassword = PasswordUtil.encryptPassword(salt, newPassword);
        user.setPassword(newDbPassword);
        if (!updateById(user)) {
            throw new MicroServiceBusinessException(UserServiceErrorMessageConstants.CHANGE_PASSWORD_FAILED);
        }
    }

    /**
     * 校验用户旧密码
     *
     * @param context
     */
    private void checkOldPassword(ChangePasswordContext context) {
        Long userId = context.getUserId();
        String oldPassword = context.getOldPassword();

        // 获取用户信息
        Users user = getById(userId);
        if (Objects.isNull(user)) {
            throw new MicroServiceBusinessException(UserServiceErrorMessageConstants.USER_NOT_EXISTS);
        }
        context.setEntity(user);

        //加密旧密码，与数据库中的密码进行比较
        String encryptedOldPassword = PasswordUtil.encryptPassword(user.getSalt(), oldPassword);
        if (!user.getPassword().equals(encryptedOldPassword)) {
            throw new MicroServiceBusinessException(UserServiceErrorMessageConstants.OLD_PASSWORD_NOT_MATCH);
        }
    }

    /**
     * 转换用户列表为视图对象列表
     *
     * @param userList  用户列表
     * @param checkRole 是否需要检查角色类型
     * @return 用户视图对象列表
     */
    private List<UserVO> convertUsersList(List<Users> userList, boolean checkRole) {
        if (userList == null || userList.isEmpty()) {
            return Collections.emptyList();
        }

        List<UserVO> userVOList = new ArrayList<>(userList.size());
        for (Users user : userList) {
            UserVO userVO = userConverter.usersToUserVO(user);
            if (checkRole && Objects.equals(user.getRoleId(), UserRoleEnum.ADMIN.getCode())) {
                userVO.setRole(UserRoleEnum.ADMIN.getDesc());
            } else {
                userVO.setRole(UserRoleEnum.USER.getDesc());
            }
            userVOList.add(userVO);
        }
        return userVOList;
    }

    /**
     * 生成并保存用户的accessToken
     *
     * @param context
     */
    private void generateAndSaveAccessToken(UserLoginContext context) {
        Users user = context.getEntity();
        String accessToken = JwtUtil.generateToken(user.getUsername(), UserConstants.LOGIN_USER_ID, user.getUserId(), UserConstants.ONE_DAY_LONG);

        Cache cache = cacheManager.getCache(CacheConstants.MICRO_SERVICE_CACHE_NAME);

        // 添加或更新缓存中的 accessToken，实现单机登录
        cache.put(UserConstants.USER_LOGIN_PREFIX + user.getUserId(), accessToken);

        // 将 accessToken 存入用户登录上下文中
        context.setAccessToken(accessToken);
    }

    /**
     * 校验用户登录信息
     *
     * @param context 用户登录上下文
     */
    private void checkLoginInfo(UserLoginContext context) {
        String userName = context.getUsername();
        String password = context.getPassword();

        // 从数据库中获取用户信息
        Users user = getUsersByUsername(userName);
        if (Objects.isNull(user)) {
            throw new MicroServiceBusinessException(UserServiceErrorMessageConstants.USER_NOT_EXISTS);
        }

        //获取用户的加密密码和盐值
        String salt = user.getSalt();
        String encryptedPassword = PasswordUtil.encryptPassword(salt, password);
        String dbPassword = user.getPassword();
        if (!dbPassword.equals(encryptedPassword)) {
            throw new MicroServiceBusinessException(UserServiceErrorMessageConstants.PASSWORD_NOT_MATCH);
        }

        // 将用户信息设置到登录上下文中
        context.setEntity(user);
    }

    /**
     * 根据用户名查询用户信息
     *
     * @param userName
     * @return
     */
    private Users getUsersByUsername(String userName) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("username", userName);
        return getOne(queryWrapper);
    }


    /**
     * 注册用户
     *
     * @param context 用户注册上下文
     */
    private void doRegister(UserRegisterContext context) {
        Users entity = context.getEntity();
        if (Objects.nonNull(entity)) {
            if (!save(entity)) {
                throw new MicroServiceBusinessException(UserServiceErrorMessageConstants.REGISTER_FAILED);
            }
            Result result = permissionServiceClient.bindDefaultRole(entity.getUserId());
            if (!result.isSuccess()) {
                throw new MicroServiceBusinessException(UserServiceErrorMessageConstants.BIND_DEFAULT_ROLE_FAILED);
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
        entity.setRoleId(UserRoleEnum.USER.getCode());
        entity.setCreateTime(new Date());
        entity.setUpdateTime(new Date());
        context.setEntity(entity);
    }
}




