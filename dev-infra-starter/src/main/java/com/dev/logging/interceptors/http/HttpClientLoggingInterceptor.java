package com.dev.logging.interceptors.http;

import com.dev.logging.MDCLoggingUtility;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class HttpClientLoggingInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        HttpRequest httpRequest = MDCLoggingUtility.addMDCVariablesToHttpHeaders(request);
        return execution.execute(httpRequest, body);
    }
}
