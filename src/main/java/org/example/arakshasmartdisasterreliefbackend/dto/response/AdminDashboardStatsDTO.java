package org.example.arakshasmartdisasterreliefbackend.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDashboardStatsDTO {

    private Long emergencyRequests;

    private Long activeCases;

    private Long volunteersOnline;

    private Double shelterOccupancy;
}