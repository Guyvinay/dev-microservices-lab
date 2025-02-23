package com.dev.auth.configuration;

import jakarta.annotation.PreDestroy;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.logging.Logger;

@Configuration(proxyBeanMethods = false)
@EnableAsync
public class CustomAsyncConfigurer implements AsyncConfigurer {

    private static final Logger LOGGER = Logger.getLogger(CustomAsyncConfigurer.class.getName());
    private final ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();

    public CustomAsyncConfigurer() {
        taskExecutor.setCorePoolSize(8);
        taskExecutor.setMaxPoolSize(32);
        taskExecutor.setQueueCapacity(64);
        taskExecutor.setThreadNamePrefix("ElasticSearch-Async-");
        taskExecutor.initialize();
    }

    @Override
    public Executor getAsyncExecutor() {
        return taskExecutor;
    }

    @PreDestroy
    public void destroy() {
        LOGGER.info("Shutting down Async Executor...");
        taskExecutor.shutdown();
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new CustomAsyncExceptionHandler();
    }
}
