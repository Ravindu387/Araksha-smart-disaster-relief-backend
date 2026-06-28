package org.example.arakshasmartdisasterreliefbackend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
@Data
public class ShelterRequestDTO {
    private String shelterName;

    private String address;

    private Integer totalCapacity;

    private Integer occupiedBeds;


    private Double latitude;

    private Double longitude;

    private Boolean wifi;

    private Boolean power;

    private Boolean water;
}
