package com.dev.common.annotations.aspects;

import com.dev.common.annotations.Requires;
import com.dev.common.dto.RequiresResponseDTO;
import com.dev.grpc.client.RequiresAuthorizationGrpcClient;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Method;

@Aspect
@Slf4j
@Component
public class RequiresAuthorization {

    @Autowired
    private RequiresAuthorizationGrpcClient requiresAuthorizationGrpcClient;

    @Around("@annotation(com.dev.common.annotations.Requires)")
    public Object requiresAuthorization(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();

        Method method = methodSignature.getMethod();

        Requires requires = method.getAnnotation(Requires.class);


        String privilege = requires.privilege();
        String[] actions = requires.actions();

        log.info("Checking privilege: {} with actions: {}", privilege, actions);

        RequiresResponseDTO response = requiresAuthorizationGrpcClient
                .requiresAuthorizationGrpcClient(String.join(",", actions), privilege);

        if (response != null && !response.getAllowed()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return proceedingJoinPoint.proceed(); // Proceed with method execution if access is granted
    }

}
