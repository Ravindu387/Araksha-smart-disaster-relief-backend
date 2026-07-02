package org.example.arakshasmartdisasterreliefbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HazardZoneDTO {
    private String name;
    private double latitude;
    private double longitude;
    private double radiusKm;
    private String description;
}
