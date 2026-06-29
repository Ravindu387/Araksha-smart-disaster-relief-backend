package org.example.arakshasmartdisasterreliefbackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="needs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Need {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String name;


    private String description;
}
