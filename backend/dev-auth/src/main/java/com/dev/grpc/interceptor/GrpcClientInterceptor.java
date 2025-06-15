package com.dev.grpc.interceptor;

import io.grpc.*;

public class GrpcClientInterceptor implements ClientInterceptor {
    /**
     * @param methodDescriptor 
     * @param callOptions
     * @param channel
     * @param <ReqT>
     * @param <RespT>
     * @return
     */
    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> methodDescriptor, CallOptions callOptions, Channel channel) {
        return null;
    }
}
