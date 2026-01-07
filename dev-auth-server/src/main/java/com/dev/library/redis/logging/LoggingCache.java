package com.dev.library.redis.logging;

import org.springframework.cache.Cache;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoggingCache implements Cache {

    private final Cache delegate;

    public LoggingCache(Cache delegate) {
        this.delegate = delegate;
    }

    @Override
    public ValueWrapper get(Object key) {
        ValueWrapper value = delegate.get(key);
        if (value != null) {
            log.info("Cache HIT for key: {}", key);
        } else {
            log.info("Cache MISS for key: {}", key);
        }
        return value;
    }

    // Delegate everything else to the original Cache
    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public Object getNativeCache() {
        return delegate.getNativeCache();
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        return delegate.get(key, type);
    }

    @Override
    public <T> T get(Object key, java.util.concurrent.Callable<T> valueLoader) {
        return delegate.get(key, valueLoader);
    }

    @Override
    public void put(Object key, Object value) {
        log.info("Cache PUT for key: {}", key);
        delegate.put(key, value);
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        return delegate.putIfAbsent(key, value);
    }

    @Override
    public void evict(Object key) {
        log.info("Cache EVICT for key: {}", key);
        delegate.evict(key);
    }

    @Override
    public void clear() {
        log.info("Cache CLEAR for name: {}", delegate.getName());
        delegate.clear();
    }
}


