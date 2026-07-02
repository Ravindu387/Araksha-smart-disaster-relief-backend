package org.example.arakshasmartdisasterreliefbackend.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.arakshasmartdisasterreliefbackend.entity.OtpToken;
import org.example.arakshasmartdisasterreliefbackend.entity.User;
import org.example.arakshasmartdisasterreliefbackend.repository.OtpTokenRepository;
import org.example.arakshasmartdisasterreliefbackend.repository.UserRepository;
import org.example.arakshasmartdisasterreliefbackend.service.EmailService;
import org.example.arakshasmartdisasterreliefbackend.service.ForgotPasswordService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

/**
 * ForgotPasswordServiceImpl implements the full OTP password-reset flow:
 *
 *   1. sendOtp()     → validate email exists, generate OTP, save, send email
 *   2. verifyOtp()   → check OTP is valid (exists, not expired, not used)
 *   3. resetPassword() → re-verify OTP, mark used, encode + save new password
 *
 * SecureRandom is used (NOT Math.random) to generate cryptographically
 * unpredictable OTPs as required for security.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ForgotPasswordServiceImpl implements ForgotPasswordService {

    private final UserRepository userRepository;
    private final OtpTokenRepository otpTokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;


    // SecureRandom is thread-safe and cryptographically strong
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    // ─────────────────────────────────────────────────────────────────────────
    // 1. SEND OTP
    // ─────────────────────────────────────────────────────────────────────────
    @Override
    @Transactional
    public void sendOtp(String email) {
        // Validate email exists in the system
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No account found with this email address."));

        // Clean up expired OTPs for this email before generating a new one
        otpTokenRepository.deleteAllExpired(LocalDateTime.now());

        // Generate a secure 6-digit OTP (100000 to 999999 inclusive)
        int otpInt = 100000 + SECURE_RANDOM.nextInt(900000);
        String otp = String.valueOf(otpInt);

        // Build and save the OTP entity
        // expiryTime is set automatically in @PrePersist (now + 5 minutes)
        OtpToken otpToken = OtpToken.builder()
                .email(email)
                .otp(otp)
                .used(false)
                .build();
        otpTokenRepository.save(otpToken);

        // Print to standard output so developers can test locally without real SMTP credentials configured
        System.out.println("\n==============================================");
        System.out.println("  [DEVELOPER FALLBACK] GENERATED OTP FOR: " + email);
        System.out.println("  OTP CODE: " + otp);
        System.out.println("==============================================\n");

        // Send OTP email to the user
        try {
            emailService.sendOtpEmail(email, otp);
            log.info("OTP generated and sent to: {}", email);
        } catch (Exception e) {
            log.warn("SMTP sending failed: {}. (You can copy the OTP from the console to continue local testing.)", e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 2. VERIFY OTP
    // ─────────────────────────────────────────────────────────────────────────
    @Override
    public boolean verifyOtp(String email, String otp) {
        // We do NOT mark the OTP as used here.
        // The OTP is only marked used when the password is successfully reset.
        // This allows the user to go back if they entered a wrong new password.
        boolean valid = otpTokenRepository
                .findTopByEmailAndOtpAndUsedFalseAndExpiryTimeAfterOrderByCreatedAtDesc(
                        email, otp, LocalDateTime.now())
                .isPresent();

        if (!valid) {
            log.warn("OTP verification failed for email: {}", email);
        }
        return valid;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 3. RESET PASSWORD
    // ─────────────────────────────────────────────────────────────────────────
    @Override
    @Transactional
    public void resetPassword(String email, String otp, String newPassword) {
        // Re-verify the OTP (prevents a forged request that skips verify-otp step)
        OtpToken otpToken = otpTokenRepository
                .findTopByEmailAndOtpAndUsedFalseAndExpiryTimeAfterOrderByCreatedAtDesc(
                        email, otp, LocalDateTime.now())
                .orElseThrow(() -> new RuntimeException("Invalid or expired OTP. Please request a new one."));

        // Mark the OTP as used so it cannot be reused
        otpToken.setUsed(true);
        otpTokenRepository.save(otpToken);

        // Retrieve the user and encode + save the new password
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No account found with this email address."));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Clean up all OTPs for this email now that password is reset
        otpTokenRepository.deleteAllByEmail(email);
        log.info("Password successfully reset for: {}", email);
    }
}
