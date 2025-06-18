package com.guangyin.redis.test.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guangyin.core.constants.MicroServiceConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * redis cache 配置类,不支持事务
 */
@SpringBootConfiguration
@EnableCaching
@Slf4j
@ComponentScan(value = MicroServiceConstants.BASE_COMPONENT_SCAN_PATH + ".cache.redis")
public class RedisCacheConfigTest {
    /**
     * 规定程序与redis数据传递序列化方式，防止出现乱码
     * @param redisConnectionFactory
     * @return
     */
    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory redisConnectionFactory)
    {
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        RedisTemplate<String,Object> template = new RedisTemplate<>();
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);

        template.setConnectionFactory(redisConnectionFactory);
        //设置key和value的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        template.setValueSerializer(jackson2JsonRedisSerializer);
        //设置hash key和hash value的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);
        template.setHashValueSerializer(jackson2JsonRedisSerializer);

        return template;
    }

    /**
     * 配置并返回一个 Redis 缓存管理器 (RedisCacheManager)，用于管理 Redis 缓存。
     * 该方法通过 RedisConnectionFactory 创建 Redis 缓存管理器，并配置缓存的序列化方式。
     *
     * @param redisConnectionFactory Redis 连接工厂，用于创建与 Redis 的连接
     * @return RedisCacheManager 配置好的 Redis 缓存管理器
     */
    @Bean
    public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {

        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);

        // 配置 Redis 缓存的默认设置，包括键和值的序列化方式
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration
                .defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer));
        // 创建并配置 Redis 缓存管理器
        RedisCacheManager cacheManager = RedisCacheManager
                .builder(RedisCacheWriter.lockingRedisCacheWriter(redisConnectionFactory))
                .cacheDefaults(redisCacheConfiguration)// 设置默认的缓存配置
                .transactionAware() // 启用事务支持，确保缓存操作与事务一致
                .build();
        log.info("redis cache manager is loaded successfully");
        return cacheManager;
    }
}
