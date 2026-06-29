package org.example.arakshasmartdisasterreliefbackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="emergency_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmergencyRequestVolunteers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(unique=true)
    private String requestCode;



    private String title;


    private String description;


    private String citizenName;


    private String contact;


    private String address;


    private String priority;


    private String status;



    private Double latitude;


    private Double longitude;



    @OneToMany(
            mappedBy="emergencyRequest",
            cascade=CascadeType.ALL
    )
    @Builder.Default
    private List<Task> tasks=new ArrayList<>();



    @OneToMany(
            mappedBy="emergencyRequest",
            cascade=CascadeType.ALL
    )
    @Builder.Default
    private List<EmergencyNeed> needs=new ArrayList<>();
}
