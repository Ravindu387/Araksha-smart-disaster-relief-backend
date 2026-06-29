package org.example.arakshasmartdisasterreliefbackend.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmergencyResponseVolunteers {
    private String requestCode;


    private String title;


    private String description;


    private String citizenName;


    private String contact;


    private String address;


    private String priority;


    private String distance;
}
