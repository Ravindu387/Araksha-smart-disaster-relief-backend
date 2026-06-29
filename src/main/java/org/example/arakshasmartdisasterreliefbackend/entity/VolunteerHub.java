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

public class VolunteerHub {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "volunteer_code", nullable = false, unique = true)
        private String volunteerCode;

        @Column(nullable = false)
        private String name;

        @Column(nullable = false)
        private String email;

        @Column(nullable = false, unique = true)
        private String phone;

        @Column(nullable = false)
        private String status;

        @Column(name = "current_latitude")
        private Double currentLatitude;

        @Column(name = "current_longitude")
        private Double currentLongitude;

        @Column(nullable = false)
        private Boolean available;

        @OneToMany(mappedBy = "volunteer",
                cascade = CascadeType.ALL,
                fetch = FetchType.LAZY)
        @Builder.Default
        private List<Task> tasks = new ArrayList<>();

        @OneToOne(mappedBy = "volunteer",
                cascade = CascadeType.ALL,
                fetch = FetchType.LAZY)
        private Performance performance;

}

