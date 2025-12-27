package com.dev.utility;

import com.dev.service.SystemBootstrapService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@ConditionalOnProperty(
        prefix = "app.bootstrap",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = false
)
@RequiredArgsConstructor
public class SystemBootstrapRunner implements ApplicationRunner {

    private final SystemBootstrapService bootstrapService;

    @Override
    public void run(ApplicationArguments args) {
        log.info("Starting system bootstrap...");
        bootstrapService.bootstrap();
        log.info("System bootstrap completed.");
    }
}
