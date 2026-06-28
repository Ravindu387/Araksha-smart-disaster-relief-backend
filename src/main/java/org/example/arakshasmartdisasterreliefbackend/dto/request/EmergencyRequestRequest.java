package org.example.arakshasmartdisasterreliefbackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmergencyRequestRequest {

    @NotBlank(message = "Request ID is required")
    private String requestId;

    @NotBlank(message = "Citizen Name is required")
    private String citizenName;

    @NotBlank(message = "Emergency Type is required")
    private String emergencyType;

    @NotBlank(message = "Priority is required")
    private String priority;

    @NotBlank(message = "Status is required")
    private String status;

    @NotBlank(message = "Location is required")
    private String location;

    private String assignedVolunteer;

}