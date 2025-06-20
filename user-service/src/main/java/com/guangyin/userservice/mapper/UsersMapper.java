package com.guangyin.userservice.mapper;

import com.guangyin.userservice.entity.Users;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author zjz
* @description 针对表【users_0(用户信息表)】的数据库操作Mapper
* @createDate 2025-06-18 18:50:47
* @Entity com.guangyin.userservice.entity.Users
*/
@Mapper
public interface UsersMapper extends BaseMapper<Users> {


}




