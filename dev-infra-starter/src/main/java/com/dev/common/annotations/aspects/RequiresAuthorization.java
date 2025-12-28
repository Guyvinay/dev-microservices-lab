package com.dev.common.annotations.aspects;

import com.dev.common.annotations.Requires;
import com.dev.common.dto.RequiresResponseDTO;
import com.dev.dto.privilege.Action;
import com.dev.dto.privilege.Privilege;
import com.dev.dto.privilege.PrivilegeActionPair;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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

        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();

        Method method = methodSignature.getMethod();

        Requires requires = method.getAnnotation(Requires.class);
        Map<Privilege, Set<Action>> request = new HashMap<>();

        for (Requires.Require require : requires.value()) {
            Action[] actions = require.actions();
            Privilege privilege = require.privilege();
            request.put(privilege, new HashSet<>(List.of(actions)));
        }
        log.info("Privilege request: {}",  request);
        RequiresResponseDTO response = requiresAuthorizationGrpcClient
                .requiresAuthorizationGrpcClient(request);

        if (response != null && !response.getAllowed()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return proceedingJoinPoint.proceed(); // Proceed with method execution if access is granted
    }

}
