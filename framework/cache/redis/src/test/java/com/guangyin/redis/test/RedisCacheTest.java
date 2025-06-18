package com.guangyin.redis.test;

import cn.hutool.core.lang.Assert;
import com.guangyin.cachecore.constants.CacheConstants;
import com.guangyin.redis.test.instance.CacheAnnotationTester;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

@SpringBootTest(classes = RedisCacheTest.class)
@SpringBootApplication
public class RedisCacheTest {
    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private CacheAnnotationTester cacheAnnotationTester;

    /**
     * 简单测试CacheManager的功能和获取Cache对象的功能
     */
    @Test
    public void caffeineCacheManagerTest() {
        Cache cache = cacheManager.getCache(CacheConstants.MICRO_SERVICE_CACHE_NAME);
        Assert.notNull(cache);
        cache.put("name", "value");
        String value = cache.get("name", String.class);
        Assert.isTrue("value".equals(value));
    }

    /**
     * 测试缓存注解是否生效
     */
    @Test
    public void caffeineCacheAnnotationTest() {
        for (int i = 0; i < 2; ++i) {
            cacheAnnotationTester.testCacheable("guangyin");
        }
    }
}
