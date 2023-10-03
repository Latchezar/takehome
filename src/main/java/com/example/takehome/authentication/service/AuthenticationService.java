package com.example.takehome.authentication.service;

import com.example.takehome.authentication.infrastructure.CreateTokenRequest;
import com.example.takehome.authentication.infrastructure.JwtResponse;

public interface AuthenticationService {

    JwtResponse createToken(CreateTokenRequest createTokenRequest);
}
