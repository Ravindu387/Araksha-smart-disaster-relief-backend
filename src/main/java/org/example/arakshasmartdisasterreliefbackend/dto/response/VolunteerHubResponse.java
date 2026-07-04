package org.example.arakshasmartdisasterreliefbackend.dto.response;

import lombok.*;
import java.util.List;

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


    private String email;


    private String phone;

    private String address;

    private String district;

    private List<String> skills;

}
