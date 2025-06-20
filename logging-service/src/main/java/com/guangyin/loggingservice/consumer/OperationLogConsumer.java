package com.guangyin.loggingservice.consumer;

import com.alibaba.fastjson.JSON;
import com.guangyin.loggingservice.entity.OperationLog;
import com.guangyin.loggingservice.service.OperationLogsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RocketMQMessageListener(
        topic = "operation-log-topic",
        consumerGroup = "logging-consumer-group"
)
public class OperationLogConsumer implements RocketMQListener<Map<String, Object>> {

    @Autowired
    private OperationLogsService operationLogsService;

    @Override
    public void onMessage(Map<String, Object> message) {
        try {
            OperationLog operationLog = new OperationLog();

            // FIX: Handle number conversion safely
            Number logId = (Number) message.get("log_id");
            Number userId = (Number) message.get("user_id");

            if (logId != null) {
                operationLog.setLogId(logId.longValue());
            }
            if (userId != null) {
                operationLog.setUserId(userId.longValue());
            }

            operationLog.setAction((String) message.get("action"));
            operationLog.setIp((String) message.get("ip"));
            operationLog.setDetail((String) message.get("detail"));

            operationLogsService.save(operationLog);
            log.info("成功记录操作日志: {}", JSON.toJSONString(operationLog));
        } catch (Exception e) {
            log.error("记录操作日志失败", e);
        }
    }
}