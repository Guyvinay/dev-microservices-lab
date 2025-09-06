package com.dev.restTemplate;

import org.elasticsearch.client.RestClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

public class CustomRestTemplate<T> extends RestTemplate {

    private String uri;
    private HttpMethod httpMethod;
    private Objects requestBody;
    private HttpHeaders httpHeaders;
    private MediaType mediaType;
    private ParameterizedTypeReference<T> responseType;

    public CustomRestTemplate(String uri) {
        this.uri = uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public CustomRestTemplate<T> setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
        return this;
    }

    public CustomRestTemplate<T> setRequestBody(Objects requestBody) {
        this.requestBody = requestBody;
        return this;
    }

    public CustomRestTemplate<T> setHttpHeaders(HttpHeaders httpHeaders) {
        this.httpHeaders = httpHeaders;
        return this;
    }

    public CustomRestTemplate<T> setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
        return this;
    }

    public CustomRestTemplate<T> setResponseType(ParameterizedTypeReference<T> responseType) {
        this.responseType = responseType;
        return this;
    }

    public String getUri() {
        return uri;
    }

    public Objects getRequestBody() {
        return requestBody;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public HttpHeaders getHttpHeaders() {
        return httpHeaders;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public ParameterizedTypeReference<T> getResponseType() {
        return responseType;
    }
}
