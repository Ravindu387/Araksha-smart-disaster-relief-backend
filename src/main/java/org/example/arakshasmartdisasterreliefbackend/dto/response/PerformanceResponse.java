package org.example.arakshasmartdisasterreliefbackend.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerformanceResponse {
    private Integer response;


    private Integer feedback;


    private Integer completion;


    private Integer communication;


    private Integer safety;
}
