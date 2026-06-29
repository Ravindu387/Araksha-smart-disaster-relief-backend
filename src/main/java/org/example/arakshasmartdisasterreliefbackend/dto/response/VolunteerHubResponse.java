package org.example.arakshasmartdisasterreliefbackend.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VolunteerHubResponse {
    private Long id;


    private String volunteerCode;


    private String name;


    private String status;


    private Boolean available;

}
