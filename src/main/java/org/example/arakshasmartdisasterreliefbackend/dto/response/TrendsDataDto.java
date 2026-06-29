package org.example.arakshasmartdisasterreliefbackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * DTO for the monthly trends line chart data.
 *
 * Each field is a list of 6 numbers — one per month (Jan–Jun).
 * These directly feed the Chart.js line chart datasets.
 *
 * Example JSON:
 * {
 *   "months": ["Jan","Feb","Mar","Apr","May","Jun"],
 *   "flood":      [50, 62, 90, 72, 100, 120],
 *   "fire":       [22, 18, 34, 28,  42,  38],
 *   "hurricane":  [12,  8, 22, 35,  28,  44],
 *   "earthquake": [ 8, 12, 15, 20,  18,  25]
 * }
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrendsDataDto {

    /** Month labels for the X axis, e.g. ["Jan","Feb","Mar","Apr","May","Jun"] */
    private List<String> months;

    /** Monthly flood incident counts */
    private List<Long> flood;

    /** Monthly fire incident counts */
    private List<Long> fire;

    /** Monthly hurricane incident counts */
    private List<Long> hurricane;

    /** Monthly earthquake incident counts */
    private List<Long> earthquake;

    /** Monthly average response times in minutes (for the bar chart) */
    private List<Double> avgResponse;
}
