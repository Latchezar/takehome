package com.example.takehome.authentication;

import com.example.takehome.authentication.infrastructure.JwtResponse;
import com.example.takehome.config.security.SecurityUser;
import com.example.takehome.model.Authority;
import com.example.takehome.model.TakehomeUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class TokenManager {

    private static final String USERNAME_CLAIM = "username";
    private static final String AUTHORITIES_CLAIM = "authorities";
    private static final Long TOKEN_MAX_AGE = 7_200_000L;

    @Value("${jwt.token.secretKey}")
    private String secretKey;

    public JwtResponse createToken(TakehomeUser user) {
        long currentTime = System.currentTimeMillis();

        List<String> authorities = user.getAuthorities().stream()
                                       .map(Authority::getName).toList();

        String token = Jwts.builder()
                           .setId(UUID.randomUUID().toString())
                           .setSubject(String.valueOf(user.getId()))
                           .claim(USERNAME_CLAIM, user.getUsername())
                           .claim(AUTHORITIES_CLAIM, authorities)
                           .setIssuedAt(new Date(currentTime))
                           .setExpiration(new Date(currentTime + TOKEN_MAX_AGE))
                           .signWith(SignatureAlgorithm.HS512, secretKey.getBytes())
                           .compact();
        return JwtResponse.builder()
                          .token(token)
                          .issuedAt(currentTime)
                          .expiresAt(currentTime + TOKEN_MAX_AGE)
                          .build();
    }

    public SecurityUser getUserFromToken(String token) {
        if (isValid(token)) {
            Claims tokenClaims = getClaimsFromToken(token).getBody();
            List<String> tokenAuthorities = ((List<String>) tokenClaims.get(AUTHORITIES_CLAIM));
            List<SimpleGrantedAuthority> authorities = tokenAuthorities.stream().map(SimpleGrantedAuthority::new).toList();
            return new SecurityUser(Long.valueOf(tokenClaims.getSubject()),
                                    (String) tokenClaims.get(USERNAME_CLAIM),
                                    StringUtils.EMPTY, authorities);
        }
        return null;
    }

    private boolean isValid(String token) {
        try {
            getClaimsFromToken(token);
            return true;
        } catch (io.jsonwebtoken.JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Jws<Claims> getClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(token);
    }

}
