package org.example.arakshasmartdisasterreliefbackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDashboardResourceDTO {

    private String resourceName;

    private Integer available;

    private Integer total;

    private Double usagePercentage;

}