package org.example.arakshasmartdisasterreliefbackend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.arakshasmartdisasterreliefbackend.enums.Role;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegisterRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Role role;
    private String phone;
    private String location;
    private java.util.List<String> skills;
    private String profilePhotoUrl;
    private String idVerificationDocUrl;
}

