
package com.dev.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching // Enables @Cacheable, @CacheEvict, etc.
public class RedisConfig {

    private static final Logger log = LoggerFactory.getLogger(RedisConfig.class);

    @Value("${app.redis.key-prefix:dev-auth:}")
    private String keyPrefix;

    @Value("${app.redis.cache.default-ttl-minutes:15}")
    private long defaultTtlMinutes;

    @Bean
    @ConditionalOnMissingBean(ObjectMapper.class)
    public ObjectMapper redisObjectMapper() {
        ObjectMapper om = new ObjectMapper();
        // Support Java 8 date/time types (Instant, LocalDateTime, etc.)
        om.registerModule(new JavaTimeModule());
        // Prefer ISO strings (readable) over timestamps
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // Make private fields visible to serializer (no need for getters)
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);

        return om;
    }

    @Bean
    public RedisSerializer<String> redisKeySerializer() {
        return new StringRedisSerializer();
    }

    @Bean
    public RedisSerializer<Object> redisValueSerializer(ObjectMapper objectMapper) {
        return new GenericJackson2JsonRedisSerializer(objectMapper);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory lettuceConnectionFactory,
                                                       RedisSerializer<String> redisKeySerializer,
                                                       RedisSerializer<Object> redisValueSerializer) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(lettuceConnectionFactory);

        redisTemplate.setKeySerializer(redisKeySerializer);
        redisTemplate.setHashKeySerializer(redisKeySerializer);

        redisTemplate.setValueSerializer(redisValueSerializer);
        redisTemplate.setHashValueSerializer(redisValueSerializer);

        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    /**
     * Base cache configuration applied to all caches unless overridden.
     * - Default TTL of 15 minutes
     * - Prevents null values from being cached
     * - Uses String keys (human-readable in Redis)
     * - Uses JSON for values (portable & DevTools-safe)
     */
    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .prefixCacheNameWith(keyPrefix + ":")  // prefix every cache name with configured namespace
                .entryTtl(Duration.ofMinutes(defaultTtlMinutes)) // Default TTL for ALL caches
                .disableCachingNullValues()       // Avoid storing null results
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new StringRedisSerializer() // Store keys as plain strings
                        )
                )
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new GenericJackson2JsonRedisSerializer() // Store values as JSON
                        )
                );
    }


    /**
     * RedisCacheManager with custom TTLs for specific caches.
     * This allows different caches to have different expiration times.
     *
     * Example:
     * - "auth:user" cache → 30 min TTL (user profiles rarely change)
     * - "auth:roles" cache → 10 min TTL (roles may change more often)
     * - All others → default 15 min TTL
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {

        // Get base configuration
        RedisCacheConfiguration defaultConfig = cacheConfiguration();

        // Define custom TTL configurations for specific cache names
        Map<String, RedisCacheConfiguration> cacheConfigs = new HashMap<>();
        cacheConfigs.put("dev-auth:user", defaultConfig.entryTtl(Duration.ofMinutes(1))); // 30 min TTL
        cacheConfigs.put("auth:roles", defaultConfig.entryTtl(Duration.ofMinutes(10))); // 10 min TTL

        RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(cacheConfiguration())
                .withInitialCacheConfigurations(cacheConfigs) // Per-cache TTL overrides
                .transactionAware(); // apply cache ops only after successful transaction commit

        return builder.build();
    }

    /**
     * CacheErrorHandler: prevents Redis errors from bubbling to callers.
     * Production best-practice: log the error and allow the application to proceed without cache.
     */
    @Bean
    public CacheErrorHandler cacheErrorHandler() {
        return new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
                log.warn("Redis GET error for cache='{}' key='{}'. Proceeding without cache. Cause: {}",
                        cache != null ? cache.getName() : "unknown", key, exception.getMessage());
            }

            @Override
            public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
                log.warn("Redis PUT error for cache='{}' key='{}'. Cause: {}",
                        cache != null ? cache.getName() : "unknown", key, exception.getMessage());
            }

            @Override
            public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
                log.warn("Redis EVICT error for cache='{}' key='{}'. Cause: {}",
                        cache != null ? cache.getName() : "unknown", key, exception.getMessage());
            }

            @Override
            public void handleCacheClearError(RuntimeException exception, Cache cache) {
                log.warn("Redis CLEAR error for cache='{}'. Cause: {}",
                        cache != null ? cache.getName() : "unknown", exception.getMessage());
            }
        };
    }

}