package org.example.arakshasmartdisasterreliefbackend.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompleteTaskRequest {
    private Long taskId;


    private Integer rating;
}
