package com.dev.service;

import com.dev.security.dto.AccessJwtToken;
import com.dev.utility.grpc.RequiresRequest;

import java.util.List;
import java.util.UUID;

public interface AuthorizationEvaluator {
    boolean isAllowed(
            RequiresRequest request
    );
}
