package org.example.arakshasmartdisasterreliefbackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "scheduler_jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchedulerJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String jobKey;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String cronExpression;

    private LocalDateTime lastRun;

    private LocalDateTime nextRun;

    @Column(nullable = false)
    private String status; // ACTIVE, PAUSED

    @Builder.Default
    private Integer totalRuns = 0;

    @Builder.Default
    private Integer failedRuns = 0;

    private String lastRunStatus; // SUCCESS, FAILED
}
