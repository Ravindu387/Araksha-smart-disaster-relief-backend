package org.example.arakshasmartdisasterreliefbackend.controller;

import lombok.RequiredArgsConstructor;
import org.example.arakshasmartdisasterreliefbackend.dto.request.ForgotPasswordRequest;
import org.example.arakshasmartdisasterreliefbackend.dto.request.ResetPasswordRequest;
import org.example.arakshasmartdisasterreliefbackend.dto.request.VerifyOtpRequest;
import org.example.arakshasmartdisasterreliefbackend.service.ForgotPasswordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST Controller for the Forgot Password OTP flow.
 *
 * All three endpoints are under /api/auth/ so they are covered by the
 * existing permitAll() rule in SecurityConfig — no further config needed
 * for authentication. They are also covered by the CSRF ignore rule.
 *
 * Endpoints:
 *   POST /api/auth/forgot-password  → validate email, generate & send OTP
 *   POST /api/auth/verify-otp       → validate OTP (not expired, not used)
 *   POST /api/auth/reset-password   → re-verify OTP, encode and save new password
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class ForgotPasswordController {

    private final ForgotPasswordService forgotPasswordService;

    // ─────────────────────────────────────────────────────────────────────────
    // POST /api/auth/forgot-password
    // ─────────────────────────────────────────────────────────────────────────
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(
            @RequestBody ForgotPasswordRequest request) {
        try {
            forgotPasswordService.sendOtp(request.getEmail());
            return ResponseEntity.ok(Map.of(
                    "message", "OTP sent to your email. Please check your inbox."
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POST /api/auth/verify-otp
    // ─────────────────────────────────────────────────────────────────────────
    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, String>> verifyOtp(
            @RequestBody VerifyOtpRequest request) {
        boolean valid = forgotPasswordService.verifyOtp(request.getEmail(), request.getOtp());
        if (valid) {
            return ResponseEntity.ok(Map.of(
                    "message", "OTP verified successfully."
            ));
        } else {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Invalid or expired OTP. Please check and try again."
            ));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POST /api/auth/reset-password
    // ─────────────────────────────────────────────────────────────────────────
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(
            @RequestBody ResetPasswordRequest request) {
        try {
            forgotPasswordService.resetPassword(
                    request.getEmail(),
                    request.getOtp(),
                    request.getNewPassword()
            );
            return ResponseEntity.ok(Map.of(
                    "message", "Password reset successfully. You can now sign in with your new password."
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
