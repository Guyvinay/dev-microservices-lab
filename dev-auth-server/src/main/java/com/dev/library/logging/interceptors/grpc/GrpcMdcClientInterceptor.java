package com.dev.library.logging.interceptors.grpc;

import com.dev.library.logging.MDCKeys;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

public class GrpcMdcClientInterceptor implements ClientInterceptor {
    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> methodDescriptor, CallOptions callOptions, Channel channel) {
        ClientCall<ReqT, RespT> delegate = channel.newCall(methodDescriptor, callOptions);
        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(delegate) {
            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                addIfPresent(headers, MDCKeys.TRACE_ID_KEY, MDC.get(MDCKeys.TRACE_ID));
                addIfPresent(headers, MDCKeys.TENANT_ID_KEY, MDC.get(MDCKeys.TENANT_ID));
                addIfPresent(headers, MDCKeys.USER_ID_KEY, MDC.get(MDCKeys.USER_ID));

                super.start(responseListener, headers);
            }
        };
    }

    private void addIfPresent(Metadata headers, Metadata.Key<String> key, String value) {
        if (StringUtils.isNotBlank(value)) {
            headers.put(key, value);
        }
    }
}
