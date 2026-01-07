package com.dev.library.redis.controller;

import com.dev.library.redis.service.CacheInspectorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/v1.0/redis/cache")
@RequiredArgsConstructor
public class CacheDebugController {

    private final CacheInspectorService cacheInspectorService;

    @GetMapping("/keys")
    public Set<String> getKeys(@RequestParam String pattern) {
        return cacheInspectorService.listKeys(pattern);
    }

    @GetMapping("/ttl")
    public Long getTTL(@RequestParam String key) {
        return cacheInspectorService.getTTL(key);
    }

    @GetMapping("/value")
    public String getValue(@RequestParam String key) {
        return cacheInspectorService.getPrettyValue(key);
    }
}