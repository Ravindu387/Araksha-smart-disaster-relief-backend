package org.example.arakshasmartdisasterreliefbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.arakshasmartdisasterreliefbackend.config.JwtService;
import org.example.arakshasmartdisasterreliefbackend.dto.request.LoginRequest;
import org.example.arakshasmartdisasterreliefbackend.dto.request.RegisterRequest;
import org.example.arakshasmartdisasterreliefbackend.dto.response.LoginResponse;
import org.example.arakshasmartdisasterreliefbackend.entity.User;
import org.example.arakshasmartdisasterreliefbackend.repository.UserRepository;
import org.example.arakshasmartdisasterreliefbackend.service.AuthService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    @Override
    public String register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return "Email already exists";
        }

        User user = new User();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        userRepository.save(user);

        return "Registration Successful";
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        String token = jwtService.generateToken(user);

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().toString());

        return response;
    }
}
