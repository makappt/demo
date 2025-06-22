package com.guangyin.userservice.common.framework.web.web.log;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 * 打印HTTP调用日志过滤器，使用者可以按需将其注入到过滤器容器中使用
 * 这里只提供基础的过滤实现
 * 使用 Spring 的 OncePerRequestFilter 作为基类，确保每个请求只被过滤一次。它的主要功能是记录 HTTP 请求和响应的日志信息，
 * 包括请求 URI、方法、参数、响应内容等。
 * NOTE HTTP 日志过滤器的执行流程：
 * 请求到达服务器：客户端发送 HTTP 请求到服务器，请求会首先经过 Servlet 容器的过滤器链。
 * CORS 过滤器处理：CorsFilter 优先级较高（@Order(1)），首先拦截请求并添加跨域相关响应头，确保浏览器允许跨域访问。
 * HTTP 日志过滤器处理：HttpLogFilter 优先级较低（@Order(Integer.MAX_VALUE)），在过滤器链的最后处理请求，记录请求和响应的详细信息。
 * 业务逻辑处理：请求通过过滤器链后，进入目标资源（如 Spring Controller）进行业务逻辑处理。
 * 响应返回：响应经过过滤器链逆向返回，最终送达客户端。
 *
 */
@WebFilter(filterName = "httpLogFilter")
@Slf4j
@Order(Integer.MAX_VALUE)
public class HttpLogFilter extends OncePerRequestFilter {

    /**
     * 实现过滤器逻辑，记录 HTTP 请求和响应的日志信息
     *
     * @param request     HTTP 请求对象
     * @param response    HTTP 响应对象
     * @param filterChain 过滤器链，用于继续执行后续过滤器或目标资源
     * @throws ServletException 如果过滤过程中发生 Servlet 相关异常
     * @throws IOException      如果发生 I/O 相关异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 创建并启动一个计时器，用于记录请求处理时间
        StopWatch stopWatch = StopWatch.createStarted();
        // 包装原始请求对象，以便缓存请求体内容，支持多次读取
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        // 包装原始响应对象，以便缓存响应体内容，支持后续读取
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        // 继续执行过滤器链，将包装后的请求和响应传递给目标资源（如 Controller），触发业务逻辑处理。
        // 这一步会触发业务逻辑处理，Controller 执行业务逻辑并生成响应内容（例如通过 return 返回数据）。
        // 响应内容会被写入到 ContentCachingResponseWrapper 中（因为响应被包装，内容会被缓存）。
        // filterChain.doFilter 是阻塞调用，只有当后续过滤器和目标资源（Controller）处理完成后，控制权才会返回到当前过滤器（HttpLogFilter）。
        // 因此，filterChain.doFilter 之后的代码必然是在业务逻辑处理结束后执行。
        filterChain.doFilter(requestWrapper, responseWrapper);
        // filterChain.doFilter 调用完成后，控制权回到 HttpLogFilter 的 doFilterInternal 方法。
        // 此时，业务逻辑已经处理完毕，响应内容已经生成并存储在 responseWrapper 中。
        // 构建日志实体，读取请求和响应的详细信息（包括业务逻辑生成的响应内容）。
        HttpLogEntity httpLogEntity = HttpLogEntityBuilder.build(requestWrapper, responseWrapper, stopWatch);
        // 打印日志实体中的信息（假设 print 方法会输出日志）
        //httpLogEntity.print();
        // 将缓存的响应体内容写回到原始响应对象，确保客户端能收到响应
        // ContentCachingResponseWrapper 缓存了响应体内容，但原始的 HttpServletResponse 可能尚未写入数据（因为业务逻辑写入的是包装后的响应对象）。
        // 如果不调用 copyBodyToResponse()，客户端将无法收到响应内容，导致请求失败。
        responseWrapper.copyBodyToResponse();
    }
}
