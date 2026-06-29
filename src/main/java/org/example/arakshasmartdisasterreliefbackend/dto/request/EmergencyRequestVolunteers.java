package org.example.arakshasmartdisasterreliefbackend.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmergencyRequestVolunteers {
    private String requestCode;


    private String title;


    private String description;


    private String citizenName;


    private String contact;


    private String address;


    private String priority;


    private String status;


    private Double latitude;


    private Double longitude;
}
