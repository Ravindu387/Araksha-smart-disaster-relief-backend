package org.example.arakshasmartdisasterreliefbackend.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmergencyRequestResponse {

    private Long id;

    private String requestId;

    private String citizenName;

    private String emergencyType;

    private String priority;

    private String status;

    private String location;

    private String assignedVolunteer;

    private LocalDateTime requestTime;

    private List<String> resources;

}