package com.guangyin.loggingservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guangyin.loggingservice.entity.OperationLog;
import com.guangyin.loggingservice.service.OperationLogsService;
import com.guangyin.loggingservice.mapper.OperationLogsMapper;
import org.springframework.stereotype.Service;

/**
* @author zjz
* @description 针对表【operation_logs】的数据库操作Service实现
* @createDate 2025-06-21 02:35:06
*/
@Service
public class OperationLogsServiceImpl extends ServiceImpl<OperationLogsMapper, OperationLog>
    implements OperationLogsService{

}




