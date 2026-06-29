package org.example.arakshasmartdisasterreliefbackend.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStatsResponse {
    private Integer completedTasks;


    private Integer activeTasks;


    private Integer openRequests;


    private Double rating;

}
