package org.example.arakshasmartdisasterreliefbackend.controller;

import lombok.RequiredArgsConstructor;
import org.example.arakshasmartdisasterreliefbackend.dto.request.LoginRequest;
import org.example.arakshasmartdisasterreliefbackend.dto.request.RegisterRequest;
import org.example.arakshasmartdisasterreliefbackend.dto.response.LoginResponse;
import org.example.arakshasmartdisasterreliefbackend.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin
public class AuthController {
    private final AuthService authService;


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {

        return ResponseEntity.ok(authService.register(request));

    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request,
            jakarta.servlet.http.HttpServletResponse response) {

        LoginResponse loginResponse = authService.login(request);

        jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("token", loginResponse.getToken());
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // true in prod profile
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(cookie);

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(jakarta.servlet.http.HttpServletResponse response) {
        jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("token", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return ResponseEntity.ok("Logout Successful");
    }
}