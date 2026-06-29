package org.example.arakshasmartdisasterreliefbackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(unique = true, nullable = false)
    private String taskCode;


    private String name;


    private String location;


    private String distance;


    private String eta;


    private String priority;


    private String status;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="volunteer_id")
    private Volunteer volunteer;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="emergency_request_id")
    private EmergencyRequest emergencyRequest;
}
