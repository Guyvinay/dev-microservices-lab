package com.dev.library.logging.interceptors.http;

import com.dev.library.logging.constant.MDCLoggingUtility;
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

        HttpRequest requestWithMdcHeaders = MDCLoggingUtility.addMDCVariablesToHeaders(request);


        return execution.execute(requestWithMdcHeaders, body);
    }
}
