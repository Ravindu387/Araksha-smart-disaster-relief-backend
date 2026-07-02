package org.example.arakshasmartdisasterreliefbackend.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.arakshasmartdisasterreliefbackend.dto.request.LoginRequest;
import org.example.arakshasmartdisasterreliefbackend.dto.request.RegisterRequest;
import org.example.arakshasmartdisasterreliefbackend.dto.response.LoginResponse;
import org.example.arakshasmartdisasterreliefbackend.entity.User;
import org.example.arakshasmartdisasterreliefbackend.repository.UserRepository;
import org.example.arakshasmartdisasterreliefbackend.service.AuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class AuthController {
    private final AuthService authService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse response) {

        LoginResponse loginResponse = authService.login(request);

        // 1. Set HttpOnly Cookie
        ResponseCookie jwtCookie = ResponseCookie.from("token", loginResponse.getToken())
                .httpOnly(true)
                .secure(false) // Set to true in prod/HTTPS
                .path("/")
                .maxAge(3600) // 1 hour
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());

        // 2. Set Session & SecurityContext
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);
        if (user != null) {
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authToken);
            SecurityContextHolder.setContext(context);

            HttpSession session = httpRequest.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", context);
        }

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            HttpServletRequest request,
            HttpServletResponse response) {

        // 1. Invalidate Session
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // 2. Clear Security Context
        SecurityContextHolder.clearContext();

        // 3. Clear Token Cookie
        ResponseCookie jwtCookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0) // delete cookie
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());

        return ResponseEntity.ok("Logout Successful");
    }
}