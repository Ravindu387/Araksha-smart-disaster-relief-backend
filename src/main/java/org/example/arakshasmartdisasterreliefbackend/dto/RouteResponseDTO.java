package org.example.arakshasmartdisasterreliefbackend.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteResponseDTO {
    private String startAddress;
    private String endAddress;
    private String polyline;
    private Double distanceKm;
    private Double durationMinutes;
    private List<LatLngDTO> coordinates;
}
