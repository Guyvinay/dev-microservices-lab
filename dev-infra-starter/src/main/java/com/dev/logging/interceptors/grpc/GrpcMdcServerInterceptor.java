package com.dev.logging.interceptors.grpc;

import com.dev.logging.MDCLoggingUtility;
import io.grpc.ForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;

public class GrpcMdcServerInterceptor implements ServerInterceptor {

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall, Metadata metadata, ServerCallHandler<ReqT, RespT> serverCallHandler) {
        MDCLoggingUtility.populateMdcFromGrpcMetadata(metadata);

        ServerCall.Listener<ReqT> delegate = serverCallHandler.startCall(serverCall, metadata);

        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT>(delegate) {
            @Override
            public void onComplete() {
                MDCLoggingUtility.removeVariablesFromMDCContext();
                super.onComplete();
            }

            @Override
            public void onCancel() {
                MDCLoggingUtility.removeVariablesFromMDCContext();
                super.onCancel();
            }
        };
    }

}
