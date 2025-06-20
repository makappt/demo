package com.guangyin.userservice.common.aspect;

import com.alibaba.fastjson.JSON;
import com.guangyin.core.response.Result;
import com.guangyin.core.utils.IdUtil;
import com.guangyin.core.utils.JwtUtil;
import com.guangyin.userservice.common.annotation.OperationLog;
import com.guangyin.userservice.common.utils.UserIdUtil;
import com.guangyin.userservice.constants.UserConstants;
import com.guangyin.web.log.HttpLogEntityBuilder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.apache.rocketmq.spring.core.RocketMQTemplate;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Aspect
@Component
public class OperationLogAspect {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Pointcut("@annotation(com.guangyin.userservice.common.annotation.OperationLog)")
    public void operationLog() {
    }

    @Around("operationLog()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        OperationLog operationLog = method.getAnnotation(OperationLog.class);

        Long userId = null;
        boolean isSuccess = result instanceof Result && ((Result<?>) result).isSuccess();
        //登录和注册操作无法通过localThread获取用户ID，需要特殊处理
        if (isSuccess) {
            if ("login".equals(operationLog.action())) {
                String token = (String) ((Result<?>) result).getData();
                if (token != null) {
                    Object idObject = JwtUtil.analyzeToken(token, UserConstants.LOGIN_USER_ID);
                    userId = Long.valueOf(String.valueOf(idObject));
                }
            } else if ("register".equals(operationLog.action())) {
                userId = (Long) ((Result<?>) result).getData();
            }
        }
        // 如果用户ID仍然为null，则是其他操作，尝试从ThreadLocal获取
        if (userId == null) {
            userId = UserIdUtil.get();
        }

        Map<String, Object> logMap = new HashMap<>();
        logMap.put("log_id", IdUtil.get());
        logMap.put("user_id", userId);
        logMap.put("action", operationLog.action());

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            logMap.put("ip", HttpLogEntityBuilder.getIpAddress(request));

            Map<String, Object> detail = new HashMap<>();
            detail.put("method", request.getMethod());
            detail.put("request_uri", request.getRequestURI());
            detail.put("request_params", joinPoint.getArgs());
            detail.put("response_data", result);
            detail.put("resolve_time", (endTime - startTime) + "ms");
            logMap.put("detail", JSON.toJSONString(detail));

            rocketMQTemplate.convertAndSend("operation-log-topic", logMap);
        }

        return result;
    }
}