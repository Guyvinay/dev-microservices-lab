package com.dev.redis.aspect;

import com.dev.redis.annotation.RedisCacheAdapter;
import com.dev.utility.TenantContextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class RedisCacheAdapterAspect {

    private final CacheManager cacheManager;

    @Around(value = "@annotation(com.dev.redis.annotation.RedisCacheAdapter)")
    public Object redisCacheAdapterAspect(ProceedingJoinPoint joinPoint) throws Throwable {
        String tenantId = TenantContextUtil.getTenantId();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        RedisCacheAdapter redisCacheAdapter = method.getAnnotation(RedisCacheAdapter.class);
        String key = generateKey(tenantId, joinPoint);

        RedisCacheAdapter.RedisCacheOperation operation = redisCacheAdapter.operation();
        Cache cache = cacheManager.getCache(key);
        if (cache == null) {
            throw new IllegalArgumentException("Cache not configured " + key);
        }

        return switch (operation) {
            case CACHE -> cacheable(cache, key, joinPoint, redisCacheAdapter);
            case PUT -> put(cache, key, joinPoint, redisCacheAdapter);
            case EVICT -> {
                evict(cache, key, joinPoint, redisCacheAdapter);
                yield joinPoint.proceed();
            }
        };
    }

    private void evict(Cache cache, String key, ProceedingJoinPoint joinPoint, RedisCacheAdapter redisCacheAdapter) {
        if (redisCacheAdapter.allEntries()) {
            cache.clear();
            if (redisCacheAdapter.log()) {
                log.info("[RedisCacheAdapter] EVICT ALL entries in {}", cache.getName());
            }
        } else {
            cache.evict(key);
            if (redisCacheAdapter.log()) {
                log.info("[RedisCacheAdapter] EVICT for key: {}", key);
            }
        }
    }

    private Object put(Cache cache, String key, ProceedingJoinPoint joinPoint, RedisCacheAdapter redisCacheAdapter) throws Throwable {
        Object result = joinPoint.proceed();

        cache.put(key, result);
        if(redisCacheAdapter.log()) {
            log.info("[RedisCacheAdapter] PUT {} -> {}", key, result);
        }

        return result;
    }


    private Object cacheable(Cache cache, String key, ProceedingJoinPoint pjp, RedisCacheAdapter redisCacheAdapter) throws Throwable {
        Object value = cache.get(key, Object.class);
        if (value != null) {
            if(redisCacheAdapter.log()) {
                log.info("[RedisCacheAdapter] HIT {} -> {}", key, value);
            }
            return value;
        }
        Object result = pjp.proceed();
        cache.put(key, result);
        if(redisCacheAdapter.log()) {
            log.info("[RedisCacheAdapter] MISS {} -> cached", key);
        }
        return result;
    }

    private String generateKey(String tenantId, ProceedingJoinPoint pjp) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        RedisCacheAdapter redisCacheAdapter = method.getAnnotation(RedisCacheAdapter.class);
        String name = redisCacheAdapter.name();
        String cacheName = StringUtils.isEmpty(name) ? method.getName() : name;
        Object[] args = pjp.getArgs();
        String paramKey = Arrays.stream(args).map(String::valueOf).collect(Collectors.joining(":"));
        return String.format("%s:%s:%s", tenantId, cacheName, paramKey);
    }
}
