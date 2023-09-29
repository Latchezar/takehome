package com.example.takehome.keycloak;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.BearerAuthFilter;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.token.TokenManager;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

import static org.keycloak.OAuth2Constants.CLIENT_CREDENTIALS;

@Configuration
@EnableConfigurationProperties
public class KeycloakConfig {

    @Bean
    public Keycloak keycloak(final KeycloakAdminClientConfig keycloakProperties) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        ConnectionPool okHttpConnectionPool = new ConnectionPool(50, 60, TimeUnit.SECONDS);
        builder.connectionPool(okHttpConnectionPool);
        builder.connectTimeout(10, TimeUnit.SECONDS);
        builder.readTimeout(60, TimeUnit.SECONDS);
        builder.retryOnConnectionFailure(true);
        OkHttpClient client = builder
                .build();

        return KeycloakBuilder.builder()
                              .serverUrl(keycloakProperties.getUrl())
                              .resteasyClient(new ResteasyClientBuilderImpl().httpEngine(new OkHttpClientEngine(client)).build())
                              .realm(keycloakProperties.getRealm())
                              .username(keycloakProperties.getAdminUsername())
                              .password(keycloakProperties.getAdminPassword())
                              .grantType(CLIENT_CREDENTIALS)
                              .clientId(keycloakProperties.getClient())
                              .clientSecret(keycloakProperties.getSecret())
                              .build();
    }

    @Bean
    public RealmResource defaultRealm(final Keycloak keycloak,
                                      final KeycloakAdminClientConfig keycloakProperties) {
        return keycloak.realm(keycloakProperties.getRealm());
    }

    @Bean
    public TokenManager keycloakTokenManager(final Keycloak keycloak) {
        return keycloak.tokenManager();
    }

    @Bean
    public BearerAuthFilter keycloakBearerAuthFilter(final TokenManager keycloakTokenManager) {
        return new BearerAuthFilter(keycloakTokenManager);
    }
}
