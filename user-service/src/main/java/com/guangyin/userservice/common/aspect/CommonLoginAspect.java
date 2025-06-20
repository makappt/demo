package com.guangyin.userservice.common.aspect;

import com.guangyin.cachecore.constants.CacheConstants;
import com.guangyin.core.response.ResponseCode;
import com.guangyin.core.response.Result;
import com.guangyin.core.utils.JwtUtil;
import com.guangyin.userservice.common.annotation.LoginIgnore;
import com.guangyin.userservice.common.utils.UserIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.guangyin.userservice.constants.UserConstants;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 统一登录拦截校验切面逻辑实现类
 *
 * <p>
 * NOTE 项目中web请求的执行流程：
 * 客户端请求 → CorsFilter(@Order(1)) → HttpLogFilter(@Order(Integer.MAX_VALUE)) →
 * DispatcherServlet → Spring拦截器 → AOP切面(loginAuthAround) → Controller方法 → 响应返回
 *
 * <p>
 * NOTE统一拦截登录校验执行流程：
 *  当 POINT_CUT 表达式匹配的控制器方法被调用时
 *  Spring 会拦截该调用，并将控制权转交给 loginAuthAround() 方法
 *  loginAuthAround() 可以在目标方法执行前执行登录验证逻辑
 *  通过 proceedingJoinPoint.proceed() 执行原始方法
 *  可以在目标方法执行后执行后续处理
 */
@Component
@Aspect
@Slf4j
public class CommonLoginAspect {
    /**
     * 登录认证参数名称
     */
    private static final String LOGIN_AUTH_PARAM_NAME = "authorization";

    /**
     * 请求头登录认证key
     */
    private static final String LOGIN_AUTH_REQUEST_HEADER_NAME = "Authorization";

    /**
     * NOTE 切面表达式
     *  格式：execution(* 包名.类名.方法名(..))
     */
    private final static String POINT_CUT = "execution(* com.guangyin.userservice.controller..*(..))";

    @Autowired
    private CacheManager cacheManager;

    /**
     * @Pointcut 方法 (loginAuth()) 只是一个标记(切入点)，它的方法体通常为空，主要作用是定义拦截规则,供其他通知使用。
     */
    @Pointcut(value = POINT_CUT)
    public void loginAuth() {

    }

    /**
     * 登录认证切面环绕通知，负责在目标方法执行前后执行额外的代码,表示在登录认证切面方法执行前后都会执行该方法。
     * 1. 判断需不需要校验登录信息
     * 2.校验登录信息
     * a.从请求头中获取token
     * b.从缓存中获取token
     * c. 解析token
     * d.解析userid存入线程上下文对象中
     */
    @Around("loginAuth()")
    public Object loginAuthAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        // // 判断是否需要校验登录信息（检查方法上是否有@LoginIgnore注解）
        if (cheakNeedCheckInfo(proceedingJoinPoint)) {
            // 获取当前请求的相关属性
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            String requestURI = request.getRequestURI();
            log.info("请求URI: {}", requestURI);
            // 检查用户是否已登录并保存用户ID到线程上下文
            if (!checkAndSaveUserId(request)) {
                log.warn("请求URI: {} 检测到未登录，跳转到登录界面", requestURI);
                return Result.fail(ResponseCode.NEED_LOGIN);
            }
            log.info("请求URI: {} 检测到已登录，继续执行", requestURI);
        }
        // 执行原始方法并返回其结果,不进行后续处理
        return proceedingJoinPoint.proceed();
    }

    /**
     * 检查并保存用户ID
     * <p>
     * NOTE: 通过jwt技术解析token，解决分布式环境下的用户登录状态问题。
     *  jwt技术常配合拦截器AOP一起使用
     *
     * @param request
     * @return
     */
    private boolean checkAndSaveUserId(HttpServletRequest request) {
        String accessToken = request.getHeader(LOGIN_AUTH_REQUEST_HEADER_NAME);
        if (StringUtils.isBlank(accessToken)) {
            accessToken = request.getParameter(LOGIN_AUTH_PARAM_NAME);
        }
        if (StringUtils.isBlank(accessToken)) {
            return false;
        }
        // 解析token
        Object userId = JwtUtil.analyzeToken(accessToken, UserConstants.LOGIN_USER_ID);
        if (Objects.isNull(userId)) {
            return false;
        }
        // 从缓存中获取token
        Cache cache = cacheManager.getCache(CacheConstants.MICRO_SERVICE_CACHE_NAME);
        Cache.ValueWrapper valueWrapper = cache.get(UserConstants.USER_LOGIN_PREFIX + userId);
        if (Objects.isNull(valueWrapper)) {
            return false;
        }
        Object redisAccessToken = valueWrapper.get();
        // 如果缓存中没有对应的token，说明用户未登录或登录已过期
        if (Objects.isNull(redisAccessToken)) {
            return false;
        }
        //比较缓存中的token和请求头中的token是否一致
        if (Objects.equals(accessToken, redisAccessToken)) {
            // 如果一致，将用户ID存入线程上下文对象中
            saveUserIdToThreadLocal(userId);
            return true;
        }
        return false;
    }

    /**
     * 将用户ID保存到线程上下文对象中
     *
     * @param userId
     */
    private void saveUserIdToThreadLocal(Object userId) {
        UserIdUtil.set(Long.valueOf(String.valueOf(userId)));
    }

    /**
     * 判断是否需要校验登录信息
     *
     * @param proceedingJoinPoint AOP连接点，包含被拦截方法的信息
     * @return true表示需要校验登录信息，false表示不需要校验登录信息
     */
    private boolean cheakNeedCheckInfo(ProceedingJoinPoint proceedingJoinPoint) {
        // 获取连接点签名信息
        Signature signature = proceedingJoinPoint.getSignature();
        // 将签名转换为方法签名
        MethodSignature methodSignature = (MethodSignature) signature;
        // 获取当前拦截的方法对象
        Method method = methodSignature.getMethod();
        // 判断方法上是否有@LoginIgnore注解，如果有则不需要验证(返回false)，没有则需要验证(返回true)
        return !method.isAnnotationPresent(LoginIgnore.class);
    }

}
