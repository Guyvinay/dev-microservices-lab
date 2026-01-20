package com.dev.service.impl;

import com.dev.entity.ShortUrlEntity;
import com.dev.repository.ShortUrlRepository;
import com.dev.service.Base62Encoder;
import com.dev.service.ShortUrlCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShortUrlCodeServiceImpl implements ShortUrlCodeService {
    private final ShortUrlRepository repository;
    private final Base62Encoder base62Encoder;
    @Override
    public ShortUrlEntity generateAndPersistCode(ShortUrlEntity entity) {
        // 1. Persist first to get ID
        ShortUrlEntity saved = repository.save(entity);

        // 2. Encode DB ID
        String shortCode = base62Encoder.encode(saved.getId());

        // 3. Assign & save
        saved.setShortCode(shortCode);
        return repository.save(saved);

    }
}
