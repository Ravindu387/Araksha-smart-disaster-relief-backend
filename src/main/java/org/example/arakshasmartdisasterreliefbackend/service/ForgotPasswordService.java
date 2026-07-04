package org.example.arakshasmartdisasterreliefbackend.service;

public interface ForgotPasswordService {

    void sendOtp(String email);

    void verifyOtp(String email, String otp);

    void resetPassword(String email, String otp, String newPassword);
}