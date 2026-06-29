package org.example.arakshasmartdisasterreliefbackend.dto.response;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private String email;
    private String role;
}
