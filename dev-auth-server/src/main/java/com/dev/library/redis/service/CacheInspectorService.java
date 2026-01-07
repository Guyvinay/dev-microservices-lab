package com.dev.library.redis.service;

import com.dev.utility.AuthContextUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheInspectorService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Lists all keys in Redis matching a pattern.
     * For example: "auth:user::*"
     */
    public Set<String> listKeys(String pattern) {
        return redisTemplate.keys(pattern);
    }

    /**
     * Gets the TTL (in seconds) for a given key.
     */
    public Long getTTL(String key) {
        return redisTemplate.getExpire(key);
    }

    /**
     * Fetches and pretty prints the cached JSON value for debugging.
     */
    public String getPrettyValue(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return null;
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(value);
        } catch (Exception e) {
            return value.toString(); // fallback
        }
    }

    /**
     * Generic method to fetch and deserialize a cached value into a Java type.
     */
    public <T> T getValue(String key, Class<T> type) {
        log.info("redis getValue called");
        String tenantId = AuthContextUtil.getTenantId();

        String _key = tenantId + "::" + key;
        Object value = redisTemplate.opsForValue().get(_key);
        if (value == null) {
            log.warn("value not found for key: {}", _key);
            return null;
        }
        log.info("redis returned value for key: {}", _key);
        try {
            // If already of type T (when RedisTemplate uses same serializer)
            if (type.isInstance(value)) {
                return type.cast(value);
            }
            // Else, try converting from JSON
            return objectMapper.convertValue(value, type);
        } catch (Exception e) {
            log.error("Failed to deserialize Redis value for key: {}", e.getMessage(), e);
            throw new RuntimeException(
                    "Failed to deserialize Redis value for key: " + _key, e
            );
        }
    }

    /**
     * Store a value in Redis with no TTL.
     */
    public <T> void setValue(String key, T value) {
        log.info("Setting in redis called: ");

        String tenantId = AuthContextUtil.getTenantId();

        String _key = tenantId + "::" + key;
        log.info("Setting in redis with key: {}", _key);
        redisTemplate.opsForValue().set(_key, value);
    }

    /**
     * Store a value in Redis with a TTL in seconds.
     */
    public <T> void setValue(String key, T value, long ttlSeconds) {
        redisTemplate.opsForValue().set(key,value, Duration.ofSeconds(ttlSeconds));
    }

}