package org.example.arakshasmartdisasterreliefbackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "shelter")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Shelter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false)
    private Integer occupied;

    @Column(length = 20)
    private String status;

    @Column(columnDefinition = "TEXT")
    private String amenities;

    @Column(precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(precision = 11, scale = 8)
    private BigDecimal longitude;

    @Column(name = "last_updated", insertable = false, updatable = false)
    private LocalDateTime lastUpdated;


}