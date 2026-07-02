package org.example.arakshasmartdisasterreliefbackend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NearbyShelterDTO {
    private Integer shelterId;
    private String name;
    private String address;
    private Double distanceKm;
    private Double durationMinutes;
    private Integer capacity;
    private Integer occupied;
    private String status;
    private Double latitude;
    private Double longitude;
}
