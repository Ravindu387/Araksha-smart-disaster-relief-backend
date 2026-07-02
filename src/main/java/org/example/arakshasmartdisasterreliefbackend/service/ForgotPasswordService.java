package org.example.arakshasmartdisasterreliefbackend.service;

/**
 * Service interface for the Forgot Password OTP flow.
 * Three operations: send OTP, verify OTP, reset password.
 */
public interface ForgotPasswordService {

    /**
     * Checks email exists, generates OTP, stores it, sends it by email.
     * @param email The user's registered email address
     */
    void sendOtp(String email);

    /**
     * Validates the OTP for an email (exists, not expired, not used).
     * Does NOT mark as used here — reset-password does the final check+mark.
     * @param email The user's email
     * @param otp   The OTP code entered by the user
     * @return true if valid
     */
    boolean verifyOtp(String email, String otp);

    /**
     * Re-validates the OTP, marks it as used, encodes and saves new password.
     * @param email       The user's email
     * @param otp         The OTP (re-verified for security)
     * @param newPassword The new plain-text password (will be BCrypt encoded)
     */
    void resetPassword(String email, String otp, String newPassword);
}
