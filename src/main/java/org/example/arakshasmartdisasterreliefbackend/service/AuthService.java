package org.example.arakshasmartdisasterreliefbackend.service;

import org.example.arakshasmartdisasterreliefbackend.dto.request.RegisterRequest;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    String register(RegisterRequest request);
}
