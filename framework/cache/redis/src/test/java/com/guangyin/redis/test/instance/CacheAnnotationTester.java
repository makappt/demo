package com.guangyin.redis.test.instance;

import com.guangyin.cachecore.constants.CacheConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * Cache 测试类
 */
@Slf4j
@Component
public class CacheAnnotationTester {
    /**
     * 测试缓存注解
     *
     * sync=true表示在缓存未命中时，框架只会放出一个线程去查询数据，解决了一部分缓存穿透的问题。
     *
     * NOTE 缓存穿透场景：
     * 1.缓存未命中，大量请求同时到达，导致数据库压力过大。可以使用sync=true来解决。查询后添加缓存。
     * 2.恶意请求，攻击者请求不存在的数据，导致数据库压力过大。可以使用布隆过滤器来解决。查询后无法添加缓存。
     *
     * @param name 名称
     * @return 返回值
     */
    @Cacheable(cacheNames = CacheConstants.MICRO_SERVICE_CACHE_NAME,key="#name",sync = true)
    public String testCacheable(String name)
    {
        log.info("call com.guangyin.pan.cache.redis.test.instance.CacheAnnotationTester.testCacheable,parameter name:{}", name);
        return new StringBuilder("hello ").append(name).toString();
    }
}
