package org.example.arakshasmartdisasterreliefbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Represents a single emergency incident.
 *
 * type           → category of the disaster (FLOOD, FIRE, HURRICANE, EARTHQUAKE, MEDICAL, OTHER)
 * status         → whether it is OPEN or RESOLVED
 * reportedAt     → when the incident was first reported
 * resolvedAt     → when it was marked resolved (null if still OPEN)
 * responseTimeMinutes → how many minutes from report to first volunteer arrival
 */
@Entity
@Table(name = "incidents")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Disaster type — stored as a VARCHAR in the DB.
     * Valid values: FLOOD, FIRE, HURRICANE, EARTHQUAKE, MEDICAL, OTHER
     */
    @Column(nullable = false, length = 50)
    private String type;

    /**
     * Current status of the incident.
     * Valid values: OPEN, RESOLVED
     */
    @Column(nullable = false, length = 20)
    private String status;

    /**
     * Date and time when the incident was first reported.
     */
    @Column(nullable = false)
    private LocalDateTime reportedAt;

    /**
     * Date and time when the incident was resolved.
     * This is NULL if the incident is still OPEN.
     */
    @Column(nullable = true)
    private LocalDateTime resolvedAt;

    /**
     * How many minutes it took for a volunteer to arrive after the report.
     * This can be NULL if the incident is still OPEN.
     */
    @Column(nullable = true)
    private Integer responseTimeMinutes;
}
