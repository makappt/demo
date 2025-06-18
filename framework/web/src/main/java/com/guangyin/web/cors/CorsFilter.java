package com.guangyin.web.cors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * CORS过滤器，通过拦截HTTP请求并添加跨域相关的响应头，允许前端跨域访问后端资源。
 *
 * NOTE WebFilter 和 Order 注解的作用
 * WebFilter用于定义一个名称为corsFilter的Servlet过滤器，Spring Boot会自动扫描并注册该过滤器
 * Order注解用于定义过滤器的执行顺序，值越小优先级越高
 *
 * NOTE CORS过滤器执行流程：
 * 1. 当一个HTTP请求到达服务器时，CorsFilter 过滤器会首先拦截该请求（因为 @Order(1) 优先级较高）。
 * 2. 在 doFilter 方法中，调用 addCorsResponseHeader 方法，向响应中添加CORS头信息。
 * 3. 完成CORS头信息的添加后，请求继续通过 filterChain.doFilter 传递给下一个过滤器或目标资源。(在这个项目中，会传递到HttpLogFilter)
 * 4. 最终，响应返回给客户端时，包含了CORS头信息，浏览器会根据这些头信息决定是否允许跨域请求。
 */
@WebFilter(filterName = "corsFilter")
@Order(1)
public class CorsFilter implements Filter {

    /**
     * 过滤器初始化方法
     * @param servletRequest http请求
     * @param servletResponse http响应
     * @param filterChain 过滤器链,用于将请求传递给下一个过滤器或目标资源
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // 将 ServletResponse 转换为 HttpServletResponse，以便操作HTTP响应头
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        // 向响应中添加CORS相关的头信息
        addCorsResponseHeader(response);
        // 将请求和响应传递给过滤器链中的下一个过滤器或最终的Servlet处理。
        filterChain.doFilter(servletRequest, servletResponse);
    }

    /**
     * 添加跨域的响应头
     */
    private void addCorsResponseHeader(HttpServletResponse response) {
        response.setHeader(CorsConfigEnum.CORS_ORIGIN.getKey(), CorsConfigEnum.CORS_ORIGIN.getValue());
        response.setHeader(CorsConfigEnum.CORS_CREDENTIALS.getKey(), CorsConfigEnum.CORS_CREDENTIALS.getValue());
        response.setHeader(CorsConfigEnum.CORS_METHODS.getKey(), CorsConfigEnum.CORS_METHODS.getValue());
        response.setHeader(CorsConfigEnum.CORS_MAX_AGE.getKey(), CorsConfigEnum.CORS_MAX_AGE.getValue());
        response.setHeader(CorsConfigEnum.CORS_HEADERS.getKey(), CorsConfigEnum.CORS_HEADERS.getValue());
    }

    /**
     * 跨域设置枚举类
     */
    @AllArgsConstructor
    @Getter
    public enum CorsConfigEnum {
        /**
         * 允许所有远程访问
         */
        CORS_ORIGIN("Access-Control-Allow-Origin", "*"),
        /**
         * 允许认证,允许发送凭据（如Cookie）
         */
        CORS_CREDENTIALS("Access-Control-Allow-Credentials", "true"),
        /**
         * 允许远程调用的请求类型,允许的HTTP方法（如 POST, GET, PATCH, DELETE, PUT）
         */
        CORS_METHODS("Access-Control-Allow-Methods", "POST, GET, PATCH, DELETE, PUT"),
        /**
         * 指定本次预检请求的有效期，单位是秒
         */
        CORS_MAX_AGE("Access-Control-Max-Age", "3600"),
        /**
         * 允许所有请求头
         */
        CORS_HEADERS("Access-Control-Allow-Headers", "*");

        private final String key;
        private final String value;
    }
}
