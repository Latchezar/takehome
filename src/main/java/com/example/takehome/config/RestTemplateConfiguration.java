package com.example.takehome.config;

import com.example.takehome.keycloak.KeycloakAdminClientConfig;
import com.example.takehome.keycloak.OkHttpClientEngine;
import lombok.RequiredArgsConstructor;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.keycloak.admin.client.Config;
import org.keycloak.admin.client.token.TokenManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
public class RestTemplateConfiguration {

    private final KeycloakAdminClientConfig keycloakAdminClientConfig;
    @Value("${request.connect.timeout}")
    private int connectTimeout;
    @Value("${request.read.timeout}")
    private int readTimeout;

    @Bean
    @Primary
    public RestTemplate restTemplate() {
        RestTemplateBuilder builder = restTemplateBuilder();
        return builder.setConnectTimeout(Duration.ofMillis(connectTimeout))
                      .setReadTimeout(Duration.ofMillis(readTimeout)).build();
    }

    @Bean
    @Qualifier("keycloak")
    public RestTemplate keycloakRestTemplate() {
        //        String serverUrl, String realm, String username, String password, String clientId, String clientSecret, String grantType, String scope
        Config config = new Config(keycloakAdminClientConfig.getUrl(),
                                   keycloakAdminClientConfig.getRealm(),
                                   keycloakAdminClientConfig.getAdminUsername(),
                                   keycloakAdminClientConfig.getAdminPassword(),
                                   keycloakAdminClientConfig.getClient(),
                                   keycloakAdminClientConfig.getSecret(),
                                   keycloakAdminClientConfig.getGrantType(),
                                   keycloakAdminClientConfig.getScope());

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        ConnectionPool okHttpConnectionPool = new ConnectionPool(50, 60, TimeUnit.SECONDS);
        builder.connectionPool(okHttpConnectionPool);
        builder.connectTimeout(10, TimeUnit.SECONDS);
        builder.readTimeout(60, TimeUnit.SECONDS);
        builder.retryOnConnectionFailure(true);
        OkHttpClient client = builder.build();
        TokenManager tokenManager = new TokenManager(config, new ResteasyClientBuilderImpl().httpEngine(new OkHttpClientEngine(client)).build());

        RestTemplateBuilder restTemplateBuilder = restTemplateBuilder();
        return restTemplateBuilder.setConnectTimeout(Duration.ofMillis(connectTimeout))
                                  .interceptors(new BearerInterceptor(tokenManager))
                                  .setReadTimeout(Duration.ofMillis(readTimeout)).build();
    }

    @Bean
    public RestTemplateBuilder restTemplateBuilder() {
        return new RestTemplateBuilder();
    }
}
