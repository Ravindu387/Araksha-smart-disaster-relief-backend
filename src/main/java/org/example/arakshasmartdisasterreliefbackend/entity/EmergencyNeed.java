package org.example.arakshasmartdisasterreliefbackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="emergency_needs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmergencyNeed {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="request_id")
    private EmergencyRequest emergencyRequest;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="need_id")
    private Need need;

    private Integer quantity;
}
