package com.example.takehome.keycloak;

import com.example.takehome.exceptions.ErrorCode;
import com.example.takehome.exceptions.ServiceException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.NotFoundException;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class KeycloakGatewayImpl implements KeycloakGateway {

    private static final String PASSWORD_PATTERN =
            "^(?!.*\\\\s)(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,40}$";
    private static final Set<String> DEFAULT_ROLES = Set.of("ROLE_USER", "OP_CREATE_BUILDING", "OP_CREATE_ESTATE_PROPERTY", "OP_MODIFY_BUILDING",
                                                            "OP_MODIFY_ESTATE_PROPERTY", "OP_SHARE_ESTATE_PROPERTY", "OP_VIEW_BUILDING");

    private final RestTemplate restTemplate;
    private final RealmResource defaultRealm;
    private final KeycloakAdminClientConfig keycloakConfig;

    public KeycloakGatewayImpl(final RestTemplate restTemplate,
                               final RealmResource defaultRealm,
                               final KeycloakAdminClientConfig keycloakConfig) {
        this.restTemplate = restTemplate;
        this.defaultRealm = defaultRealm;
        this.keycloakConfig = keycloakConfig;
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public UserRepresentation getUser(final String userId) {
        try {
            return defaultRealm.users().get(userId).toRepresentation();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceException(ErrorCode.USER_NOT_FOUND);
        }
    }

    @Override
    public List<UserRepresentation> getUsers() {
        return defaultRealm.users().list();
    }

    @Override
    public Optional<UserRepresentation> findByEmail(final String email) {
        return defaultRealm.users().search(email, true)
                           .stream()
                           .filter(u -> StringUtils.isNotBlank(u.getEmail()) && u.getEmail().equalsIgnoreCase(email))
                           .findFirst();
    }

    @Override
    public List<String> getUserRoles(String userId) {
        return defaultRealm.users().get(userId).roles().getAll().getClientMappings().get(keycloakConfig.getClient()).getMappings()
                           .stream().map(RoleRepresentation::getName).toList();
    }

    @Override
    public void updateUserStatus(String userId,
                                 boolean status) {
        try {
            UserResource userResource = defaultRealm.users().get(userId);
            UserRepresentation userRepresentation = userResource.toRepresentation();
            userRepresentation.setEnabled(status);
            userResource.update(userRepresentation);
        } catch (NotFoundException e) {
            throw new ServiceException(ErrorCode.USER_NOT_FOUND);
        }
    }
}
