package com.guangyin.loggingservice.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RocketMQMessageListener(
        topic = "user-registration-topic",
        consumerGroup = "user-service-consumer-group"
)
public class UserRegistrationConsumer implements RocketMQListener<Users> {
    @Override
    public void onMessage(Users users) {
        // 处理用户注册消息
        log.info("Received user registration message: {}", users);
    }
}
