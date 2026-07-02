package org.example.arakshasmartdisasterreliefbackend.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NearestVolunteerDTO {
    private Long volunteerId;
    private String name;
    private String phone;
    private Double rating;
    private List<String> skills;
    private String status;
    private Double distanceKm;
    private Double durationMinutes;
    private Double latitude;
    private Double longitude;
}
