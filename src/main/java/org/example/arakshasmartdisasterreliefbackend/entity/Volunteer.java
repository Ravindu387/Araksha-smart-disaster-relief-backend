package org.example.arakshasmartdisasterreliefbackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "volunteers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Volunteer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String location;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "volunteer_skills",
            joinColumns = @JoinColumn(name = "volunteer_id")
    )
    @Column(name = "skill")
    private List<String> skills = new ArrayList<>();

    private String status;

    private Double rating;

    private Integer tasks;

    private String phone;

    private String profilePhotoUrl;

    private String idVerificationDocUrl;

    private Double latitude;

    private Double longitude;
}