package com.example.takehome.authentication.infrastructure;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JwtResponse {

    private String token;
    private Long issuedAt;
    private Long expiresAt;

}
