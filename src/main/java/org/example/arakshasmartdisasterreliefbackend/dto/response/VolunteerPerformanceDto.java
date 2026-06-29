package org.example.arakshasmartdisasterreliefbackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for one volunteer in the "Top Volunteer Performance" leaderboard.
 *
 * Example JSON:
 * {
 *   "rank": 1,
 *   "name": "Sarah Connor",
 *   "avgResponseMinutes": 28,
 *   "tasksCompleted": 321,
 *   "rating": 5.0
 * }
 *
 * The Angular component will format "avgResponseMinutes" as "28 min avg response"
 * — this formatting is done in TypeScript, NOT in the backend.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VolunteerPerformanceDto {

    /** Rank position in the leaderboard (1 = highest tasks completed) */
    private int rank;

    /** Full name of the volunteer */
    private String name;

    /** Average response time in minutes (raw integer for Angular to format) */
    private int avgResponseMinutes;

    /** Total tasks (incidents) this volunteer has completed */
    private int tasksCompleted;

    /** Star rating from 1.0 to 5.0 */
    private double rating;
}
