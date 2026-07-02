package org.example.arakshasmartdisasterreliefbackend.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request body for POST /api/auth/reset-password
 * Contains the email, the verified OTP (re-checked on backend for security),
 * and the new password the user wants to set.
 */
@Getter
@Setter
@NoArgsConstructor
public class ResetPasswordRequest {
    private String email;
    private String otp;
    private String newPassword;
}
