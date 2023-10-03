package com.example.takehome.config.security;

import com.example.takehome.authentication.TokenManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final TokenManager tokenManager;

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        try {
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (!StringUtils.isEmpty((authHeader))) {
                String token = authHeader.replace("Bearer ", "");
                SecurityUser securityUser = tokenManager.getUserFromToken(token);
                SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(securityUser,
                                                                                                             null,
                                                                                                             securityUser.getAuthorities()));
            }
        } catch (Exception e) {
            log.debug("User authentication failed. Unable to find user roles!", e);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
