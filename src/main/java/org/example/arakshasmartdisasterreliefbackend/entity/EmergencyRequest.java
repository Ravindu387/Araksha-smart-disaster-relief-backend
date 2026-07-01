package org.example.arakshasmartdisasterreliefbackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "emergency_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmergencyRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String requestId;

    private String citizenName;

    private String emergencyType;

    private String priority;

    private String status;

    private String location;

    private String assignedVolunteer;

    private LocalDateTime requestTime;

    @OneToMany(
            mappedBy = "emergencyRequest",
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER
    )
    @Builder.Default
    private List<EmergencyNeed> needs = new ArrayList<>();

}