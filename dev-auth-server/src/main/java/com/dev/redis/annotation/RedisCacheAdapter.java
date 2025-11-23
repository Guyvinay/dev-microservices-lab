package com.dev.redis.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = {ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisCacheAdapter {

    String name() default "";

    long ttl() default 20;

    boolean log() default false;

    RedisCacheOperation operation() default RedisCacheOperation.CACHE;

    boolean allEntries() default false;

    enum RedisCacheOperation {
        CACHE, PUT, EVICT
    }

}
