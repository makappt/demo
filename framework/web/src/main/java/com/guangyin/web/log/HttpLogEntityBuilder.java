package com.guangyin.web.log;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.Map;
import java.util.Objects;

/**
 * HttpLogEntity构造器，用于记录 HTTP 请求和响应的日志信息
 */
public class HttpLogEntityBuilder {

    /**
     * 构建HTTP日志对象
     * 使用两个包装类：ContentCachingRequestWrapper和ContentCachingResponseWrapper
     *
     * ContentCachingRequestWrapper requestWrapper：是一个包装了 HTTP 请求的类，
     * 允许读取请求的内容（如请求体、参数等），并且支持多次读取（普通 HttpServletRequest 的请求体只能读取一次）
     * NOTE 构建HttpLogEntity的原因 是为了记录 HTTP 请求和响应的日志信息
     * HttpServletRequest 的请求体（InputStream）只能读取一次的原因主要与 Java 的 InputStream 设计以及 HTTP 请求处理的底层机制有关
     * HttpServletRequest 的请求体数据是通过 getInputStream() 方法以 InputStream 的形式提供的。而 Java 中的 InputStream 是一种流式读取机制，具有以下特性：
     * 1.流是单向的：数据从流中读取后，流的指针会向前移动，已经读取的数据无法再次读取，除非流被重置（如果流支持重置）。
     * 2.不支持重置：普通的 InputStream（如 ServletInputStream）通常不支持 reset() 操作，无法将读取指针重新移到流的开头。因此，一旦数据被读取，流的内容就无法再次获取。
     * 在 HttpServletRequest 中，getInputStream() 返回的 ServletInputStream 是一个基于网络 socket 的输入流，数据是实时从客户端传输过来的，读取后数据会被丢弃，无法重新读取。
     *简单来说就是tcp发送过来的数据，首先是发送在缓冲区里面的，在接收到数据以后，缓冲区中的数据就没了，无法再次读取，如果需要读取，就需要自己构建一个东西来保存
     *
     * @param requestWrapper 请求包装类
     * @param responseWrapper 响应包装类
     * @param stopWatch 计时器
     * @return
     */
    public static HttpLogEntity build(ContentCachingRequestWrapper requestWrapper, ContentCachingResponseWrapper responseWrapper, StopWatch stopWatch) {
        HttpLogEntity httpLogEntity = new HttpLogEntity();
        // getRemoteAddr() 是 HttpServletRequest 提供的一个标准方法，它直接返回与服务器建立 TCP 连接的客户端的 IP 地址。
        // 这个 IP 地址是服务器从 TCP 连接的网络层信息中获取的，通常是客户端的直接 IP 地址。
        // 如果客户端直接连接到服务器（没有经过代理或负载均衡器），getRemoteAddr() 返回的就是客户端的真实 IP 地址。
        // 如果客户端通过代理服务器、负载均衡器（如 Nginx、HAProxy）或 CDN 访问服务器，getRemoteAddr() 返回的将是代理服务器或负载均衡器的 IP 地址，而不是客户端的真实 IP 地址。
        // 适用于没有代理或负载均衡的简单场景，或者只需要记录直接连接方的 IP 地址。
        // 在这种情况下，通常需要通过请求头（如 X-Forwarded-For、X-Real-IP 等）来获取客户端的真实 IP 地址。
        httpLogEntity.setRequestUri(requestWrapper.getRequestURI())
                .setMethod(requestWrapper.getMethod())
                .setRemoteAddr(requestWrapper.getRemoteAddr())
                .setIp(getIpAddress(requestWrapper))
                .setRequestHeaders(getRequestHeaderMap(requestWrapper));
        if (requestWrapper.getMethod().equals(RequestMethod.GET.name())) {
            httpLogEntity.setRequestParams(JSON.toJSONString(requestWrapper.getParameterMap()));
        } else {
            httpLogEntity.setRequestParams(new String(requestWrapper.getContentAsByteArray()));
        }
        String responseContentType = responseWrapper.getContentType();
        if (StringUtils.equals("application/json;charset=UTF-8", responseContentType)) {
            httpLogEntity.setResponseData(new String(responseWrapper.getContentAsByteArray()));
        } else {
            httpLogEntity.setResponseData("Stream Body...");
        }
        httpLogEntity.setStatus(responseWrapper.getStatusCode())
                .setResponseHeaders(getResponseHeaderMap(responseWrapper))
                .setResolveTime(stopWatch.toString());
        return httpLogEntity;
    }

    /**
     * 获取IP地址，试从 HTTP 请求头（如 X-Forwarded-For、X-Real-IP 等）中解析客户端的真实 IP 地址。
     * 这些请求头通常由代理服务器或负载均衡器添加，包含了客户端的原始 IP 地址。
     * 在客户端通过代理或负载均衡器访问服务器时，getRemoteAddr() 无法获取客户端真实 IP，但代理服务器会在请求头中添加客户端的 IP 信息（如 X-Forwarded-For）。
     * getIpAddress() 方法会解析这些请求头，尝试获取客户端的真实 IP。
     * 相比 getRemoteAddr()，这种方法更适合现代互联网架构中常见的代理或负载均衡场景。
     * 如果没有找到有效的 IP 地址，则返回请求的远程地址（即代理服务器或负载均衡器的 IP 地址）。
     * 适用于有代理、负载均衡器或 CDN 的复杂网络环境，目标是获取客户端的真实 IP 地址。
     *
     * 但请求头可以被伪造，因此从请求头中获取的 IP 地址不一定完全可信，需要结合其他安全机制。
     *
     * @param request
     * @return
     */
    public static String getIpAddress(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        // 如果找不到有效的 IP 地址，则使用 getRemoteAddr() 获取远程地址
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }

    /**
     * 获取请求头MAP
     *
     * @param request
     * @return
     */
    public static Map<String, String> getRequestHeaderMap(HttpServletRequest request) {
        Map<String, String> result = Maps.newHashMap();
        if (Objects.nonNull(request)) {
            Enumeration<String> headerNames = request.getHeaderNames();
            if (Objects.nonNull(request)) {
                while (headerNames.hasMoreElements()) {
                    String headerName = headerNames.nextElement();
                    String headerValue = request.getHeader(headerName);
                    result.put(headerName, headerValue);
                }
            }
        }
        return result;
    }

    /**
     * 获取响应头MAP
     *
     * @param response
     * @return
     */
    public static Map<String, String> getResponseHeaderMap(HttpServletResponse response) {
        Map<String, String> result = Maps.newHashMap();
        if (Objects.nonNull(response)) {
            String contentType = response.getContentType();
            result.put("contentType", contentType);
        }
        return result;
    }

}
