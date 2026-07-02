package org.example.arakshasmartdisasterreliefbackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "scheduled_reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduledReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime generatedTime;

    @Column(nullable = false)
    private Integer totalEmergencies;

    @Column(nullable = false)
    private Integer activeVolunteers;

    @Column(nullable = false)
    private Double shelterOccupancy;

    @Column(nullable = false)
    private Integer lowStockResources;

    @Column(nullable = false)
    private Integer pendingRequests;
}
