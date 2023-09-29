package com.example.takehome.config.security;

import com.example.takehome.keycloak.KeycloakGateway;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class KeycloakRolesFilter extends GenericFilterBean {

    private final KeycloakGateway keycloakGateway;

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        try {
            JwtAuthenticationToken token = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
            SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(token.getToken(),
                                                                                            keycloakGateway.getUserRoles(token.getToken().getSubject())
                                                                                                           .stream()
                                                                                                           .map(SimpleGrantedAuthority::new)
                                                                                                           .collect(Collectors.toSet())));
        } catch (Exception e) {
            log.debug("User authentication failed. Unable to find user roles!", e);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
