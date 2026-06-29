package org.example.arakshasmartdisasterreliefbackend.service;

import org.example.arakshasmartdisasterreliefbackend.dto.request.LoginRequest;
import org.example.arakshasmartdisasterreliefbackend.dto.request.RegisterRequest;
import org.example.arakshasmartdisasterreliefbackend.dto.response.LoginResponse;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    String register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
}
