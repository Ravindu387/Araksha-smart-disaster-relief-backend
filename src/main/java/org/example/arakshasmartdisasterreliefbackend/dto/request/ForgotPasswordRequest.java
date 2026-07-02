package org.example.arakshasmartdisasterreliefbackend.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request body for POST /api/auth/forgot-password
 * Contains the email address the user wants to reset the password for.
 */
@Getter
@Setter
@NoArgsConstructor
public class ForgotPasswordRequest {
    private String email;
}
