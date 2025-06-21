package com.guangyin.permissionservice.common.aspect;

import com.alibaba.fastjson.JSON;
import com.guangyin.permissionservice.common.annotation.OperationLog;
import com.guangyin.permissionservice.common.framework.log.HttpLogEntityBuilder;
import com.guangyin.permissionservice.common.framework.utils.IdUtil;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class OperationLogAspect {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Pointcut("@annotation(com.guangyin.permissionservice.common.annotation.OperationLog)")
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

        // 从请求参数中获取 userId
        Object[] args = joinPoint.getArgs();
        Long userId = null;
        if (args.length > 0 && args[0] instanceof Long) {
            userId = (Long) args[0];
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