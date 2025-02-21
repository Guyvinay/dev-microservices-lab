package com.dev.auth.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "elasticsearchExecutor")
    public Executor elasticsearchExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);   // Minimum active threads
        executor.setMaxPoolSize(10);   // Maximum threads
        executor.setQueueCapacity(100); // Queue size before rejecting tasks
        executor.setThreadNamePrefix("ElasticSearch-Async-");
        executor.initialize();
        return executor;
    }
}
