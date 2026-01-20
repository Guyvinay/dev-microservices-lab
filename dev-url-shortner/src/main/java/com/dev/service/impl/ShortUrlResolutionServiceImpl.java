package com.dev.service.impl;

import com.dev.entity.ShortUrlEntity;
import com.dev.repository.ShortUrlRepository;
import com.dev.service.ShortUrlResolutionService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShortUrlResolutionServiceImpl implements ShortUrlResolutionService {

    private final ShortUrlRepository repository;

    @Override
    public String resolve(String tenantId, String shortCode) {
        Optional<ShortUrlEntity> entityOpt =
                repository.findByTenantIdAndShortCodeAndActiveTrue(
                        tenantId, shortCode
                );
        if (entityOpt.isEmpty()) {
            return "";
        }


        return "";
    }
}
