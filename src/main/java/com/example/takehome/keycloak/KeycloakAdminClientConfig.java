package com.example.takehome.keycloak;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "keycloak")
@PropertySource(value = "classpath:application.properties")
public class KeycloakAdminClientConfig {

    private String url;

    private String realm;

    private String client;

    private String secret;

    private String adminUsername;

    private String adminPassword;

    private String grantType;

    private String scope;
}
