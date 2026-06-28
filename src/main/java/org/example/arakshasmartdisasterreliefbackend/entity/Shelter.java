package org.example.arakshasmartdisasterreliefbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "Shelter")
public class Shelter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long shelterId;

    private String name;

    private String address;

    private String city;

    private Double latitude;

    private Double longitude;

    private Integer totalCapacity;

    private Integer occupiedBeds;

    private Boolean wifi;

    private Boolean water;

    private Boolean electricity;

    private String status;

    private LocalDateTime lastUpdated;
}
