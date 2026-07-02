package org.example.arakshasmartdisasterreliefbackend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeatherDTO {
    private String city;
    private Double temperature;
    private String description;
    private Double rainProbability;
    private Double windSpeed;
    private Double humidity;
    private String warnings;
}
