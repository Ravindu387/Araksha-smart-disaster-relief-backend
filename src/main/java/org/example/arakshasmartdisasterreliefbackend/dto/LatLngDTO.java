package org.example.arakshasmartdisasterreliefbackend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LatLngDTO {
    private Double latitude;
    private Double longitude;
}
