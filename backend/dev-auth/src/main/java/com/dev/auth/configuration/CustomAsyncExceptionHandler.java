package com.dev.auth.configuration;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CustomAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    private static final Logger LOGGER = Logger.getLogger(CustomAsyncExceptionHandler.class.getName());
    /**
     * @param ex
     * @param method
     * @param params
     */
    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {
        LOGGER.log(Level.SEVERE, "Exception in async method: " + method.getName(), ex);
        for (Object param: params) {
            LOGGER.log(Level.WARNING, "Parameters:  ", param);
        }
    }
}
