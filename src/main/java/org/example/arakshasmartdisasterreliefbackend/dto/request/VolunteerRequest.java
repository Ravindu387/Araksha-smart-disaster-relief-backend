package org.example.arakshasmartdisasterreliefbackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VolunteerRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Skills are required")
    private List<String> skills;

    @NotBlank(message = "Status is required")
    private String status;

    @NotNull(message = "Rating is required")
    private Double rating;

    @NotNull(message = "Tasks are required")
    private Integer tasks;

    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Phone number is required")
    private String phone;

    private String profilePhotoUrl;

    private String idVerificationDocUrl;

    private Double latitude;

    private Double longitude;
}
