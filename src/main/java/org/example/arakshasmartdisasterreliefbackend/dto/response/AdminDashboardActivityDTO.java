package org.example.arakshasmartdisasterreliefbackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDashboardActivityDTO {

    private String id;

    private String citizenName;

    private String emergencyType;

    private String location;

    private String priority;

    private String status;

    private String assignedVolunteer;

    private String requestTime;

}