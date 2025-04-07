package com.dev.common.annotations.aspects;

import com.dev.common.annotations.Requires;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Slf4j
@Component
public class RequiresAuthorization {

    @Around("@annotation(com.dev.common.annotations.Requires)")
    public Object requiresAuthorization(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();

        Method method = methodSignature.getMethod();

        Requires requires = method.getAnnotation(Requires.class);


        String privilege = requires.privilege();
        String[] actions = requires.actions();

        log.info("Checking privilege: {} with actions: {}", privilege, actions);

        return proceedingJoinPoint.proceed(); // Proceed with method execution if access is granted
    }

}
