package org.example.arakshasmartdisasterreliefbackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportsSummaryResponse {


    private String title;


    private String value;


    private String change;


    private boolean isPositive;


    private String type;
}
