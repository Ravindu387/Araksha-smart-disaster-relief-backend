package org.example.arakshasmartdisasterreliefbackend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ShelterRequestDTO {
    private Integer id;

    private String name;

    private String address;

    private Integer capacity;

    private Integer occupied;

    private String status;

    private String amenities;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private LocalDateTime lastUpdated;

    private String shelterImageUrl;
}