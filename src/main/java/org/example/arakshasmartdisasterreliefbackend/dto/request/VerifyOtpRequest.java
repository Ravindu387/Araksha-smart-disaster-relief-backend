package org.example.arakshasmartdisasterreliefbackend.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request body for POST /api/auth/verify-otp
 * Contains the email and the 6-digit OTP the user received by email.
 */
@Getter
@Setter
@NoArgsConstructor
public class VerifyOtpRequest {
    private String email;
    private String otp;
}
