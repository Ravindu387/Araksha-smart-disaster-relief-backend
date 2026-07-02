package org.example.arakshasmartdisasterreliefbackend.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VolunteerResponse {

    private Long id;

    private String name;

    private String location;

    private List<String> skills;

    private String status;

    private Double rating;

    private Integer tasks;

    private String phone;

    private String profilePhotoUrl;

    private String idVerificationDocUrl;

    private Double latitude;

    private Double longitude;
}