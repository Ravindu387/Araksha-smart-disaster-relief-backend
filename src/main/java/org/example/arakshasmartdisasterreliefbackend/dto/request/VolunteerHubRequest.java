package org.example.arakshasmartdisasterreliefbackend.dto.request;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VolunteerHubRequest {
    private String volunteerCode;


    private String name;


    private String email;


    private String phone;


    private String status;


    private Double currentLatitude;


    private Double currentLongitude;


    private String address;

    private String district;

    private List<String> skills;

    private Boolean available;
}
