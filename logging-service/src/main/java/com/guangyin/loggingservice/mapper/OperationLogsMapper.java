package com.guangyin.loggingservice.mapper;

import com.guangyin.loggingservice.entity.OperationLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author zjz
* @description 针对表【operation_logs】的数据库操作Mapper
* @createDate 2025-06-21 02:35:06
* @Entity com.guangyin.loggingservice.entity.OperationLogs
*/
@Mapper
public interface OperationLogsMapper extends BaseMapper<OperationLog> {

}




