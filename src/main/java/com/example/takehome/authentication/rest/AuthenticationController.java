package com.example.takehome.authentication.rest;

import com.example.takehome.authentication.infrastructure.CreateTokenRequest;
import com.example.takehome.authentication.infrastructure.JwtResponse;
import com.example.takehome.authentication.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/token")
    public JwtResponse createToken(@RequestBody CreateTokenRequest createTokenRequest) {
        return authenticationService.createToken(createTokenRequest);
    }
}
