package com.dev.auth.configuration;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Enables asynchronous task management. Implements {@link AsyncConfigurer} to allow
 * event publishing operations being executed both synchronously and asynchronously.
 */
//@EnableAsync
//@Configuration(proxyBeanMethods = false)
//public class AsyncConfig implements AsyncConfigurer {
//
//    /*
//     * Implementing AsyncConfigurer allows to determine an Executor for asynchronous operations only. This way it is
//     * sufficient to simply annotate desired methods with @Async for async treatment, event publishing tasks included.
//     *
//     * In contrast, providing an ApplicationEventMulticaster would cause all event publishing tasks to be executed
//     * asynchronously by default. To be able to execute synchronous tasks nevertheless, we would have to adapt
//     * ApplicationEventMulticaster#multicastEvent() accordingly: for example, we could decide by means of a marker
//     * interface to which executor the task should get passed (https://stackoverflow.com/a/57929153).
//     */
//    @Override
//    public Executor getAsyncExecutor() {
//        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
//        taskExecutor.setCorePoolSize(8);
//        taskExecutor.setMaxPoolSize(32);
//        taskExecutor.setQueueCapacity(64);
//        taskExecutor.setThreadNamePrefix("ElasticSearch-Async-");
//        taskExecutor.initialize();
//        return taskExecutor;
//    }
//
//    @Override
//    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
//        return new SimpleAsyncUncaughtExceptionHandler();
//    }
//}