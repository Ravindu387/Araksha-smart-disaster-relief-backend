package org.example.arakshasmartdisasterreliefbackend.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskRequest {
    private String taskCode;


    private String name;


    private String location;


    private String distance;


    private String eta;


    private String priority;


    private String status;


    private Long volunteerId;


    private Long emergencyRequestId;
}
