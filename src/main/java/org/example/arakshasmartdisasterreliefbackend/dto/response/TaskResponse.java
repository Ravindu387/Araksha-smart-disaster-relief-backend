package org.example.arakshasmartdisasterreliefbackend.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskResponse {
    private Long id;


    private String taskCode;


    private String name;


    private String location;


    private String distance;


    private String eta;


    private String priority;


    private String status;
}
