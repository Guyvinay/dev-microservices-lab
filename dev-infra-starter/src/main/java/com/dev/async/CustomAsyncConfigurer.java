package com.dev.async;

import com.dev.logging.MdcTaskDecorator;
import jakarta.annotation.PreDestroy;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Logger;

@Configuration(proxyBeanMethods = false)
@EnableAsync
public class CustomAsyncConfigurer implements AsyncConfigurer {

    private static final Logger LOGGER = Logger.getLogger(CustomAsyncConfigurer.class.getName());
    private ThreadPoolTaskExecutor taskExecutor;

    @Bean(name = "threadPoolTaskExecutor")
    public Executor threadPoolTaskExecutor(MdcTaskDecorator mdcTaskDecorator) {
        taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(16);
        taskExecutor.setMaxPoolSize(32);
        taskExecutor.setQueueCapacity(2000);
        taskExecutor.setThreadNamePrefix("threadPoolTaskExecutor-");
        taskExecutor.setTaskDecorator(mdcTaskDecorator);

        taskExecutor.setRejectedExecutionHandler(
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
        taskExecutor.initialize();

        // To delegate current Security context in async thread from ThreadLocal
        // If no need then just return taskExecutor
        return new DelegatingSecurityContextAsyncTaskExecutor(taskExecutor);
    }

    @Bean
    public MdcTaskDecorator mdcTaskDecorator() {
        return new MdcTaskDecorator();
    }

    @Override
    public Executor getAsyncExecutor() {
        return taskExecutor;
    }

    @PreDestroy
    public void destroy() {
        LOGGER.info("Shutting down Async Executor...");
        if (taskExecutor != null) {
            taskExecutor.shutdown();
        }
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new CustomAsyncExceptionHandler();
    }
}
