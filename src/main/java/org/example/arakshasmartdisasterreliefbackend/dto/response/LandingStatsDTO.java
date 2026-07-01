package org.example.arakshasmartdisasterreliefbackend.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LandingStatsDTO {
    private long activeEmergencyRequests;
    private long criticalIncidents;
    private long inProgressIncidents;
    private long resolvedIncidents;
    private long volunteersActive;
    private long reliefSheltersCount;
}
