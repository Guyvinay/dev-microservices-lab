package com.dev.service;

import com.dev.entity.ShortUrlEntity;

public interface ShortUrlCodeService {
    ShortUrlEntity generateAndPersistCode(ShortUrlEntity entity);
}
