package com.dev.common.annotations.aspects;

import com.dev.common.annotations.Requires;
import com.dev.common.dto.RequiresResponseDTO;
import com.dev.dto.privilege.Action;
import com.dev.dto.privilege.Privilege;
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
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

@Aspect
@Slf4j
@Component
public class RequiresAuthorization {

    @Autowired
    private RequiresAuthorizationGrpcClient requiresAuthorizationGrpcClient;

    @Around("@annotation(com.dev.common.annotations.Requires)")
    public Object requiresAuthorization(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = signature.getMethod();
        Requires requires = method.getAnnotation(Requires.class);

        Map<Privilege, Set<Action>> request = new EnumMap<>(Privilege.class);

        for (Requires.Require require : requires.value()) {
            request.put(require.privilege(), Set.of(require.actions()));
        }

        log.info("Authorization request | method={} | required={}", method.getName(), request);

        RequiresResponseDTO response = requiresAuthorizationGrpcClient.requiresAuthorizationGrpcClient(request);

        if (response == null || !response.getAllowed()) {
            log.warn("Authorization denied | method={} | required={}", method.getName(), request);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        log.info("Authorization granted | method={}", method.getName());

        return proceedingJoinPoint.proceed();
    }

}
