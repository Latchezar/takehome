package com.example.takehome.config;

import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.keycloak.admin.client.token.TokenManager;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.util.List;

public class BearerInterceptor implements ClientHttpRequestInterceptor {
    public static final String AUTH_HEADER_PREFIX = "Bearer ";
    private final String tokenString;
    private final TokenManager tokenManager;

    public BearerInterceptor(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
        this.tokenString = null;
    }

    @NotNull
    public ClientHttpResponse intercept(@NotNull HttpRequest request,
                                        @NotNull byte[] body,
                                        @NotNull ClientHttpRequestExecution execution) throws IOException {
        String authHeader = this.tokenManager != null ? this.tokenManager.getAccessTokenString() : this.tokenString;
        if (authHeader != null && !authHeader.startsWith(AUTH_HEADER_PREFIX)) {
            authHeader = AUTH_HEADER_PREFIX + authHeader;
        }
        request.getHeaders().set("Authorization", authHeader);

        ClientHttpResponse response = execution.execute(request, body);
        if (response.getStatusCode().value() == 401 && this.tokenManager != null) {
            List<String> authHeaders = response.getHeaders().get("Authorization");
            if (CollectionUtils.isEmpty(authHeaders)) {
                return response;
            }
            for (String headerValue : authHeaders) {
                if (headerValue.startsWith(AUTH_HEADER_PREFIX)) {
                    String token = headerValue.substring(AUTH_HEADER_PREFIX.length());
                    this.tokenManager.invalidate(token);
                }
            }
        }
        return response;
    }
}
