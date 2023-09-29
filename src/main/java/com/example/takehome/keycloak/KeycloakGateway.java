package com.example.takehome.keycloak;

import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;
import java.util.Optional;

public interface KeycloakGateway {

    UserRepresentation getUser(String userId);

    List<UserRepresentation> getUsers();

    Optional<UserRepresentation> findByEmail(String email);

    List<String> getUserRoles(String userId);

    void updateUserStatus(String userId,
                          boolean status);
}
