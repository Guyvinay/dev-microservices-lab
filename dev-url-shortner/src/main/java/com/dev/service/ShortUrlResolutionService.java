package com.dev.service;

public interface ShortUrlResolutionService {
    String resolve(String tenantId, String shortCode);
}
