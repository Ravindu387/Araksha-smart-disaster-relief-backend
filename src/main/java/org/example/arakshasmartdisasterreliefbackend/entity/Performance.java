package org.example.arakshasmartdisasterreliefbackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="performance")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Performance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    private Integer response;


    private Integer feedback;


    private Integer completion;


    private Integer communication;


    private Integer safety;



    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="volunteer_id")
    private Volunteer volunteer;
}
