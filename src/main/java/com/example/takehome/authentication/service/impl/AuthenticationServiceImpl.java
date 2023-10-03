package com.example.takehome.authentication.service.impl;

import com.example.takehome.authentication.TokenManager;
import com.example.takehome.authentication.infrastructure.CreateTokenRequest;
import com.example.takehome.authentication.infrastructure.JwtResponse;
import com.example.takehome.authentication.service.AuthenticationService;
import com.example.takehome.exceptions.ErrorCode;
import com.example.takehome.exceptions.ServiceException;
import com.example.takehome.model.TakehomeUser;
import com.example.takehome.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenManager tokenManager;

    @Override
    public JwtResponse createToken(CreateTokenRequest createTokenRequest) {
        TakehomeUser user = userRepository.findByUsername(createTokenRequest.getUsername());
        if (user == null) {
            throw new ServiceException(ErrorCode.USER_NOT_FOUND);
        }

        if (!passwordEncoder.matches(createTokenRequest.getPassword(), user.getPassword())) {
            throw new ServiceException(ErrorCode.INVALID_CREDENTIALS);
        }
        return tokenManager.createToken(user);
    }
}
